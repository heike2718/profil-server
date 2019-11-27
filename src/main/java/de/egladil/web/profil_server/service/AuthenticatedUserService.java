// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.NewCookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.profil_server.ProfilServerApp;
import de.egladil.web.profil_server.domain.AuthenticatedUser;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.domain.UserSession;

/**
 * AuthenticatedUserService
 */
@RequestScoped
public class AuthenticatedUserService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticatedUserService.class);

	/**
	 * @param  userSession
	 *                     UserSession darf nicht null sein
	 * @param  user
	 *                     User kann null sein
	 * @return             AuthenticatedUser
	 */
	public AuthenticatedUser createAuthenticatedUser(final UserSession userSession, final User user) {

		AuthenticatedUser authenticatedUser = null;
		String fullName = user != null ? user.getFullName() : null;

		authenticatedUser = new AuthenticatedUser(
			UserSession.create(userSession.getSessionId(), userSession.getRoles(),
				fullName, userSession.getIdReference()),
			user);

		return authenticatedUser;
	}

	/**
	 * Erzeugt ein SessionCookie mit der gegebenen sessionId.
	 *
	 * @param  sessionId
	 *                   String darf nicht null sein
	 * @return           NewCookie
	 */
	public NewCookie createSessionCookie(final String sessionId) {

		final String name = ProfilServerApp.CLIENT_COOKIE_PREFIX + CommonHttpUtils.NAME_SESSIONID_COOKIE;

		LOG.debug("Erzeugen Cookie mit name=", name);

		// @formatter:off
		NewCookie sessionCookie = new NewCookie(name,
			sessionId,
			"/", // path
			null, // domain muss null sein, wird vom Browser anhand des restlichen Responses abgeleitet. Sonst wird das Cookie nicht gesetzt.
			1,  // version
			null, // comment
			7200, // expires (minutes)
			null,
			true, // secure
			true  // httpOnly
			);
		//@formatter:on

		return sessionCookie;

	}

}
