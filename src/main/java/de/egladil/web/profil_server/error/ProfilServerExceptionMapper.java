// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.error;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * ProfilServerExceptionMapper
 */
@Provider
public class ProfilServerExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(ProfilServerExceptionMapper.class);

	@ConfigProperty(name = "sessioncookie.domain")
	String domain;

	@Override
	public Response toResponse(final Exception exception) {

		if (exception instanceof AuthException || exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(exception.getMessage()));
			LOG.warn(exception.getMessage());
			return Response.status(401)
				.entity(payload)
				.build();
		}

		if (exception instanceof SessionExpiredException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(exception.getMessage()));
			LOG.warn(exception.getMessage());

			return Response.status(908)
				.entity(payload).cookie(CommonHttpUtils.createSessionInvalidatedCookie(domain))
				.build();
		}

		if (exception instanceof ForbiddenException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Zugang für alle User gesperrt"));
			return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
		}

		if (exception instanceof ProfilserverRuntimeException || exception instanceof ClientAuthException) {

			// wurde schon geloggt
		} else {

			LOG.error(exception.getMessage(), exception);
		}

		ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error(
			"Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de"));

		return Response.status(Status.INTERNAL_SERVER_ERROR).header("X-Checklisten-Error", payload.getMessage().getMessage())
			.entity(payload).build();
	}

}
