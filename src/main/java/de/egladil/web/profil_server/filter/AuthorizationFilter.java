// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
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

	@Context
	ResourceInfo resourceInfo;

	@Inject
	SessionService sessionService;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		Method method = resourceInfo.getResourceMethod();

		LOG.debug("entering AuthorizationFilter: method=" + method);

		if (!method.isAnnotationPresent(PermitAll.class)) {

			if (method.isAnnotationPresent(DenyAll.class)) {

				LOG.debug("DenyAll for method={}", method);
				throw new ForbiddenException();
			}

			Map<String, Cookie> cookies = requestContext.getCookies();

			Cookie sessionCookie = cookies.get(SessionUtils.NAME_SESSIONID_COOKIE);

			if (sessionCookie == null) {

				LOG.error("{}: Request ohne {}-Cookie", requestContext.getUriInfo(), SessionUtils.NAME_SESSIONID_COOKIE);
				throw new AuthException("Sie haben keine Berechtigung");
			}

			String sessionId = sessionCookie.getValue();

			UserSession userSession = sessionService.getSession(sessionId);

			if (userSession == null) {

				throw new AuthException("Sie haben keine Berechtigung");
			}

			if (method.isAnnotationPresent(RolesAllowed.class)) {

				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				String userRoles = userSession.getRoles();

				if (StringUtils.isBlank(userRoles)) {

					throw new AuthException("Sie haben keine Berechtigung");
				}

				String[] rolesToken = userRoles.split(",");

				Optional<String> optRole = Arrays.stream(rolesToken).filter(role -> rolesSet.contains(role)).findFirst();

				if (!optRole.isPresent()) {

					throw new AuthException("Sie haben keine Berechtigung");
				}

				sessionService.refresh(sessionId);
			}

		}

		LOG.info("PermitAll for method={}", method);
	}
}
