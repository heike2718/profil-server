// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.filter;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.error.AuthException;
import de.egladil.web.profil_server.service.SessionService;
import de.egladil.web.profil_server.utils.SessionUtils;

/**
 * AuthorizationFilter
 */
@ApplicationScoped
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

	private static final String STAGE_DEV = "dev";

	private static final String AUTH_HEADER = "Authorization";

	@ConfigProperty(name = "stage")
	String stage;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	SessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String path = requestContext.getUriInfo().getPath();

		LOG.debug("entering AuthorizationFilter: path={}", path);

		String sessionId = getSessionId(requestContext);

		if (sessionId != null) {

			UserSession userSession = sessionService.getSession(sessionId);

			if (userSession == null) {

				LOG.warn("sessionId {} nicht bekannt oder abgelaufen");
				throw new AuthException("keine gültige Session vorhanden");

			}

			sessionService.refresh(sessionId);
		} else {

			LOG.debug("Request ohne SessionId");
		}
	}

	private String getSessionId(final ContainerRequestContext requestContext) {

		if (!STAGE_DEV.equals(stage)) {

			Map<String, Cookie> cookies = requestContext.getCookies();

			Cookie sessionCookie = cookies.get(SessionUtils.NAME_SESSIONID_COOKIE);

			if (sessionCookie == null) {

				LOG.error("{}: Request ohne {}-Cookie", requestContext.getUriInfo(), SessionUtils.NAME_SESSIONID_COOKIE);
				return null;
			}
		}

		String authHeader = requestContext.getHeaderString(AUTH_HEADER);

		if (authHeader == null) {

			LOG.error("{} dev: Request ohne Authorization-Header", requestContext.getUriInfo());

			return null;
		}

		String sessionId = authHeader.substring(7);
		LOG.debug("sessionId={}", sessionId);
		return sessionId;

	}
}
