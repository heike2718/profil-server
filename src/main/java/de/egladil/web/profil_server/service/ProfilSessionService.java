// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.CryptoService;
import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.profil_server.dao.ResourceOwnerDao;
import de.egladil.web.profil_server.domain.ResourceOwner;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.error.AuthException;
import de.egladil.web.profil_server.error.LogmessagePrefixes;
import de.egladil.web.profil_server.error.ProfilserverRuntimeException;

/**
 * ProfilSessionService
 */
@ApplicationScoped
public class ProfilSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(ProfilSessionService.class);

	private static final int SESSION_IDLE_TIMEOUT_MINUTES = 15;

	private ConcurrentHashMap<String, UserSession> sessions = new ConcurrentHashMap<>();

	@Inject
	CryptoService cryptoService;

	@Inject
	JWTService jwtService;

	@Inject
	ResourceOwnerDao resourceOwnerDao;

	public UserSession createUserSession(final String jwt) {

		try {

			DecodedJWT decodedJWT = jwtService.verify(jwt, getPublicKey());

			String uuid = decodedJWT.getSubject();

			Claim groupsClaim = decodedJWT.getClaim(Claims.groups.name());
			String[] rolesArr = groupsClaim.asArray(String.class);

			String roles = null;

			if (rolesArr != null) {

				roles = StringUtils.join(rolesArr, ",");
			}

			String fullName = decodedJWT.getClaim(Claims.full_name.name()).asString();

			Optional<ResourceOwner> optResourceOwner = resourceOwnerDao.findByUuid(uuid);

			if (optResourceOwner.isPresent()) {

				byte[] sessionIdBase64 = Base64.getEncoder().encode(cryptoService.generateSessionId().getBytes());
				String sesionId = new String(sessionIdBase64);

				UserSession userSession = UserSession.create(sesionId, roles, fullName,
					CommonHttpUtils.createUserIdReference());
				userSession.setUuid(uuid);
				userSession.setExpiresAt(getSessionTimeout());

				sessions.put(sesionId, userSession);

				return userSession;

			} else {

				LOG.debug("kein user mit uuid=" + uuid + " bekannt");
				throw new AuthException("Das hat leider nicht geklappt.");
			}
		} catch (TokenExpiredException e) {

			LOG.error("JWT expired");
			throw new AuthException("JWT has expired");
		} catch (JWTVerificationException e) {

			LOG.warn(LogmessagePrefixes.BOT + "JWT invalid: {}", e.getMessage());
			throw new AuthException("invalid JWT");
		}

	}

	/**
	 * Gibt die Session mit der gegebenen sessionId zurück.
	 *
	 * @param  sessionId
	 *                   String
	 * @return           UserSession oder null.
	 */
	public UserSession getSession(final String sessionId) throws SessionExpiredException {

		UserSession userSession = sessions.get(sessionId);

		if (userSession != null) {

			LocalDateTime expireDateTime = CommonTimeUtils.transformFromDate(new Date(userSession.getExpiresAt()));
			LocalDateTime now = CommonTimeUtils.now();

			if (now.isAfter(expireDateTime)) {

				sessions.remove(sessionId);
				throw new SessionExpiredException("Ihre Session ist abgelaufen. Bitte loggen Sie sich erneut ein.");
			}

		}
		return userSession;
	}

	/**
	 * Verlängert die UserSession um Zeit, die eine neue Session gültig ist.
	 *
	 * @param  sessionId
	 *                   die SessionId
	 * @return           UserSession
	 */
	public UserSession refresh(final String sessionId) {

		UserSession userSession = sessions.get(sessionId);

		if (userSession != null) {

			userSession.setExpiresAt(getSessionTimeout());
			return userSession;

		} else {

			throw new SessionExpiredException("keine Session mehr vorhanden");
		}

	}

	public void invalidate(final String sessionId) {

		UserSession userSession = sessions.remove(sessionId);

		if (userSession != null) {

			LOG.info("Session invalidated: {} - {}", sessionId, userSession.getFullName());
		}

	}

	private byte[] getPublicKey() {

		try (InputStream in = getClass().getResourceAsStream("/META-INF/authprov_public_key.pem");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			return sw.toString().getBytes();
		} catch (IOException e) {

			throw new ProfilserverRuntimeException("Konnte jwt-public-key nicht lesen: " + e.getMessage());
		}

	}

	private long getSessionTimeout() {

		return CommonTimeUtils.getInterval(CommonTimeUtils.now(), SESSION_IDLE_TIMEOUT_MINUTES,
			ChronoUnit.MINUTES).getEndTime().getTime();
	}

}
