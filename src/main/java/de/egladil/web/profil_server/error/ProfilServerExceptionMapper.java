// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.error;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.profil_server.utils.SessionUtils;

/**
 * ProfilServerExceptionMapper
 */
@Provider
public class ProfilServerExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = LoggerFactory.getLogger(ProfilServerExceptionMapper.class);

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
				.entity(payload).cookie(SessionUtils.createSessionInvalidatedCookie())
				.build();
		}

		if (exception instanceof ForbiddenException) {

			ResponsePayload payload = ResponsePayload.messageOnly(MessagePayload.error("Zugang für alle User gesperrt"));
			return Response.status(Response.Status.FORBIDDEN).entity(payload).build();
		}

		// TODO Auto-generated method stub
		return null;
	}

}
