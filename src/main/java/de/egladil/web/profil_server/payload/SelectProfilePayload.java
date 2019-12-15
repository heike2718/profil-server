// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * SelectProfilePayload
 */
public class SelectProfilePayload {

	@JsonProperty
	private OAuthClientCredentials clientCredentials;

	@JsonProperty
	private String uuid;

	public static SelectProfilePayload create(final OAuthClientCredentials clientCredentials, final String uuid) {

		SelectProfilePayload result = new SelectProfilePayload();
		result.clientCredentials = clientCredentials;
		result.uuid = uuid;
		return result;

	}
}
