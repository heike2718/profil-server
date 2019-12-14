// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * ChangeProfilePasswordPayload
 */
public class ChangeProfilePasswordPayload {

	@JsonProperty
	private OAuthClientCredentials clientCredentials;

	@JsonProperty
	private ProfilePasswordPayload passwordPayload;

	@JsonProperty
	private String uuid;

	public static ChangeProfilePasswordPayload create(final OAuthClientCredentials clientCredentials, final ProfilePasswordPayload passwordPayload, final String uuid) {

		ChangeProfilePasswordPayload result = new ChangeProfilePasswordPayload();
		result.clientCredentials = clientCredentials;
		result.passwordPayload = passwordPayload;
		result.uuid = uuid;
		return result;
	}
}
