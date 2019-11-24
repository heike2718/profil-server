// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

/**
 * ChangeProfileDataPayload
 */
public class ChangeProfileDataPayload {

	private final OAuthClientCredentials clientCredentials;

	private final ProfileDataPayload profileData;

	public ChangeProfileDataPayload(final OAuthClientCredentials clientCredentials, final ProfileDataPayload profileData) {

		this.clientCredentials = clientCredentials;
		this.profileData = profileData;
	}

	public OAuthClientCredentials getClientCredentials() {

		return clientCredentials;
	}

	public ProfileDataPayload getProfileData() {

		return profileData;
	}

}
