// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.NewCookie;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.profil_server.domain.AuthenticatedUser;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.utils.SessionUtils;

/**
 * AuthenticatedUserService
 */
@RequestScoped
public class AuthenticatedUserService {

	private static final String STAGE_DEV = "dev";

	@ConfigProperty(name = "stage")
	String stage;

	@ConfigProperty(name = "sessioncookie.secure")
	boolean sessioncookieSecure;

	@ConfigProperty(name = "sessioncookie.httpOnly")
	boolean sessionCookieHttpOnly;

	@ConfigProperty(name = "sessioncookie.domain")
	String domain;

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

		if (STAGE_DEV.equals(stage)) {

			authenticatedUser = new AuthenticatedUser(
				UserSession.create(userSession.getSessionId(), userSession.getRoles(),
					fullName, userSession.getIdReference()),
				user);
		} else {

			authenticatedUser = new AuthenticatedUser(
				UserSession.create(null, userSession.getRoles(),
					fullName, userSession.getIdReference()),
				user);
		}

		return authenticatedUser;
	}

	/**
	 * Erzeugt ein SessionCookie mit der gegebenen sessionId.
	 *
	 * @param  sessionId
	 *                   Strinh darf nicht null sein
	 * @return           NewCookie
	 */
	public NewCookie createSessionCookie(final String sessionId) {

		// @formatter:off
		NewCookie sessionCookie = new NewCookie(SessionUtils.NAME_SESSIONID_COOKIE,
			sessionId,
			null,
			domain,
			1,
			null,
			7200,
			null,
			sessioncookieSecure,
			sessionCookieHttpOnly);
//		 @formatter:on
		// NewCookie sessionCookie = new NewCookie("JSESSIONID", userSession.getSessionId());

		return sessionCookie;

	}

}
