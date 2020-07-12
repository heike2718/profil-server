// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.error;

/**
 * InaccessableEndpointException
 */
public class InaccessableEndpointException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public InaccessableEndpointException(final String message, final Throwable cause) {

		super(message, cause);

	}

	public InaccessableEndpointException(final String message) {

		super(message);

	}

}
