// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * ChangeProfilePasswordPayload
 */
public class ChangeProfilePasswordPayload {

	private final OAuthClientCredentials clientCredentials;

	private final ProfilePasswordPayload passwordPayload;

	public ChangeProfilePasswordPayload(final OAuthClientCredentials clientCredentials, final ProfilePasswordPayload passwordPayload) {

		this.clientCredentials = clientCredentials;
		this.passwordPayload = passwordPayload;
	}

	public OAuthClientCredentials getClientCredentials() {

		return clientCredentials;
	}

	public ProfilePasswordPayload getPasswordPayload() {

		return passwordPayload;
	}

}
