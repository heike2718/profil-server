// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.domain;

/**
 * AuthenticatedUser
 */
public class AuthenticatedUser {

	private UserSession session;

	private User user;

	public AuthenticatedUser() {

	}

	public AuthenticatedUser(final UserSession session, final User user) {

		this.session = session;
		this.user = user;
	}

	public UserSession getSession() {

		return session;
	}

	public User getUser() {

		return user;
	}

}
