// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.endpoints;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.LogDelegate;
import de.egladil.web.commons_validation.payload.LogEntry;

/**
 * LogResource
 */
@RequestScoped
@Path("log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

	private static final Logger LOG = LoggerFactory.getLogger(LogResource.class);

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@POST
	@PermitAll
	public Response log(final LogEntry logEntry) {

		new LogDelegate().log(logEntry, LOG, clientId);

		return Response.ok().build();
	}
}
