// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import javax.validation.constraints.NotNull;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * ChangeProfilePasswordPayload
 */
public class ChangeProfilePasswordPayload {

	@NotNull
	private OAuthClientCredentials clientCredentials;

	@NotNull
	private ProfilePasswordPayload passwordPayload;

	public OAuthClientCredentials getClientCredentials() {

		return clientCredentials;
	}

	public ProfilePasswordPayload getPasswordPayload() {

		return passwordPayload;
	}

}
