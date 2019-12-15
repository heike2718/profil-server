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

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.profil_server.ProfilServerApp;
import de.egladil.web.profil_server.context.ProfilSecurityContext;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.error.AuthException;
import de.egladil.web.profil_server.error.LogmessagePrefixes;
import de.egladil.web.profil_server.service.ProfilSessionService;

/**
 * AuthorizationFilter
 */
@ApplicationScoped
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	private static final String CSRF_TOKEN_HEADER = "X-XSRF-TOKEN";

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);

	@ConfigProperty(name = "stage")
	String stage;

	@Context
	ResourceInfo resourceInfo;

	@Inject
	ProfilSessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		String path = requestContext.getUriInfo().getPath();
		String method = requestContext.getMethod();

		LOG.debug("entering AuthorizationFilter: path={}", path);

		if (!"OPTIONS".equals(method)) {

			if (needsSession(path)) {

				if (LOG.isDebugEnabled()) {

					logCookies(requestContext);
				}

				String csrfToken = requestContext.getHeaderString(CSRF_TOKEN_HEADER);
				String sessionId = CommonHttpUtils.getSessionId(requestContext, stage, ProfilServerApp.CLIENT_COOKIE_PREFIX);

				if (sessionId != null) {

					UserSession userSession = sessionService.getSession(sessionId);

					if (userSession == null) {

						LOG.warn("sessionId {} nicht bekannt oder abgelaufen", sessionId);
						throw new SessionExpiredException("keine gültige Session vorhanden");

					}

					checkCsrfToken(path, method, csrfToken, userSession);

					UserSession refreshedSession = sessionService.refresh(sessionId);
					ProfilSecurityContext securityContext = new ProfilSecurityContext(refreshedSession);
					requestContext.setSecurityContext(securityContext);

				} else {

					throw new AuthException("Keine Berechtigung");
				}
			}
		}
	}

	private void checkCsrfToken(final String path, final String method, final String csrfToken, final UserSession userSession) {

		if (!csrfTokenRequired(method)) {

			return;
		}

		if (csrfToken == null) {

			LOG.warn(LogmessagePrefixes.BOT + "Aufruf ohne CSRF-Token: path=", path);
			throw new AuthException("Keine Berechtigung");
		}

		if (!csrfToken.equals(userSession.getCsrfToken())) {

			LOG.warn(LogmessagePrefixes.BOT + "Aufruf mit falshem CSRF-Token: path=", path);
			throw new AuthException("Keine Berechtigung");
		}

	}

	private void logCookies(final ContainerRequestContext requestContext) {

		final Map<String, Cookie> cookies = requestContext.getCookies();

		System.out.println("==== Start read cookies ====");
		cookies.keySet().stream().forEach(key -> {

			Cookie cookie = cookies.get(key);
			System.out.println("[k=" + key + ", value=" + cookie.getValue() + "]");
		});
		System.out.println("==== End read cookies ====");
	}

	private boolean csrfTokenRequired(final String method) {

		return !"GET".equals(method);
	}

	private boolean needsSession(final String path) {

		return path.toLowerCase().startsWith("/profiles");
	}
}
