// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.error;

/**
 * AuthException
 */
public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public AuthException(final String message) {

		super(message);
	}

}
