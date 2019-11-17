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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.LogEntry;
import de.egladil.web.commons_validation.payload.TSLogLevel;

/**
 * LogResource
 */
@RequestScoped
@Path("log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {

	private static final Logger LOG = LoggerFactory.getLogger(LogResource.class);

	@POST
	@PermitAll
	public Response log(final LogEntry logEntry) {

		TSLogLevel level = logEntry.getLevel();
		System.out.println(level);

		switch (level) {

		case All:
		case Debug:
			LOG.debug("BrowserLog: {}", logEntry);
			break;

		case Info:
			LOG.info("BrowserLog: {}", logEntry);
			break;

		case Warn:
			LOG.warn("BrowserLog: {}", logEntry);
			break;

		case Error:
		case Fatal:
			LOG.error("BrowserLog: {}", logEntry);
			break;

		default:
			break;
		}

		return Response.ok().build();
	}
}
