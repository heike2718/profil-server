// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import javax.validation.constraints.NotNull;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * ChangeProfileDataPayload
 */
public class ChangeProfileDataPayload {

	@NotNull
	private OAuthClientCredentials clientCredentials;

	@NotNull
	private ProfileDataPayload profileData;

	public OAuthClientCredentials getClientCredentials() {

		return clientCredentials;
	}

	public ProfileDataPayload getProfileData() {

		return profileData;
	}

}
