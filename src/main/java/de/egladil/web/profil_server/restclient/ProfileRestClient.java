// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.profil_server.payload.ChangeProfileDataPayload;
import de.egladil.web.profil_server.payload.ChangeProfilePasswordPayload;
import de.egladil.web.profil_server.payload.SelectProfilePayload;

/**
 * ProfileRestClient die Base-URI ist [auth-url]/profiles
 */
@RegisterRestClient
@Path("profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProfileRestClient {

	@POST
	@Path("/profile")
	Response getUserProfile(final SelectProfilePayload selectProfilePayload);

	@DELETE
	@Path("/profile")
	Response deleteProfile(final SelectProfilePayload selectProfilePayload);

	@PUT
	@Path("/profile/password")
	public Response changePassword(final ChangeProfilePasswordPayload payload);

	@PUT
	@Path("/profile/data")
	public Response changeData(final ChangeProfileDataPayload payload);
}
