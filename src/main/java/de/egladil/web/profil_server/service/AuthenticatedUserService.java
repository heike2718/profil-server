// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.service;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.NewCookie;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.profil_server.domain.AuthenticatedUser;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.domain.UserSession;

/**
 * AuthenticatedUserService
 */
@RequestScoped
public class AuthenticatedUserService {

	private static final String STAGE_DEV = "dev";

	@ConfigProperty(name = "stage")
	String stage;

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
		NewCookie sessionCookie = new NewCookie(CommonHttpUtils.NAME_SESSIONID_COOKIE,
			sessionId,
			null, // path
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