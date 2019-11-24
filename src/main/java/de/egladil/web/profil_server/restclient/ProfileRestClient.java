// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import de.egladil.web.profil_server.payload.ChangeProfileDataPayload;
import de.egladil.web.profil_server.payload.ChangeProfilePasswordPayload;

/**
 * ProfileRestClient die Base-URI ist [auth-url]/profiles
 */
@RegisterRestClient
@Path("profiles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProfileRestClient {

	@POST
	@Path("/profile/{uuid}")
	Response getUserProfile(@PathParam(value = "uuid") final String uuid, final OAuthClientCredentials clientCredentials);

	@PUT
	@Path("/profile/{uuid}/password")
	public Response changePassword(@PathParam(
		value = "uuid") final String uuid, final ChangeProfilePasswordPayload payload);

	@PUT
	@Path("/profile/{uuid}/data")
	public Response changeData(@PathParam(value = "uuid") final String uuid, final ChangeProfileDataPayload payload);

}
