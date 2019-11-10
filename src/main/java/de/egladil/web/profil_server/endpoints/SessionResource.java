// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.endpoints;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.profil_server.domain.AuthenticatedUser;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.service.ClientAccessTokenService;
import de.egladil.web.profil_server.service.SessionService;
import de.egladil.web.profil_server.service.UserService;
import de.egladil.web.profil_server.utils.SessionUtils;

/**
 * SessionResource
 */
@RequestScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

	private static final Logger LOG = LoggerFactory.getLogger(SessionResource.class);

	@ConfigProperty(name = "auth-app.url")
	String authAppUrl;

	@ConfigProperty(name = "auth.redirect-url.login")
	String loginRedirectUrl;

	@Inject
	ClientAccessTokenService clientAccessTokenService;

	@Inject
	SessionService sessionService;

	@Inject
	UserService userService;

	@GET
	@Path("/login")
	@PermitAll
	public Response getLoginUrl() {

		String accessToken = clientAccessTokenService.orderAccessToken();

		if (StringUtils.isBlank(accessToken)) {

			return Response.serverError().entity("Fehler beim Authentisieren des Clients").build();
		}

		String redirectUrl = authAppUrl + "#/login?accessToken=" + accessToken + "&state=login&nonce=null&redirectUrl="
			+ loginRedirectUrl;

		LOG.debug(redirectUrl);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info(redirectUrl))).build();
	}

	@POST
	@Path("/session")
	@Consumes(MediaType.TEXT_PLAIN)
	@PermitAll
	public Response createSession(final String jwt) {

		LOG.debug("SessionResource === (1) " + jwt + " ===");

		UserSession userSession = sessionService.createUserSession(jwt);

		LOG.debug("SessionResource === (2) ===");

		User user = userService.getUser(userSession.getUuid());

		LOG.debug("SessionResource === (3) ===");

		AuthenticatedUser authUser = new AuthenticatedUser(userSession, user);

		// @formatter:off
		NewCookie sessionCookie = new NewCookie(SessionUtils.NAME_SESSIONID_COOKIE,
			userSession.getSessionId(),
			null,
			null,
			1,
			null,
			7200,
			null,
			false,  // nur in dev!!!
			true);
//		 @formatter:on
		// NewCookie sessionCookie = new NewCookie("JSESSIONID", userSession.getSessionId());

		// TODO: X-XSRF-Cookie anhängen

		ResponsePayload payload = new ResponsePayload(MessagePayload.info("OK"), authUser);

		return Response.ok(payload).cookie(sessionCookie).build();
	}

	public Response invalidateSession(@CookieParam(value = "sessionid") final String sessionId) {

		sessionService.invalidate(sessionId);

		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("Sie haben sich erfolreich ausgeloggt")))
			.cookie(SessionUtils.createSessionInvalidatedCookie()).build();

	}
}
