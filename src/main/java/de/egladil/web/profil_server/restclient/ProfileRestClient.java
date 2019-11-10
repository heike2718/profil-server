// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.egladil.web.commons_validation.payload.OAuthClientCredentials;

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

}
