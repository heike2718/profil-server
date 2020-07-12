// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.utils.CommonHttpUtils;
import de.egladil.web.profil_server.config.ConfigService;
import de.egladil.web.profil_server.error.AuthException;

/**
 * OriginReferrerFilter
 */
@ApplicationScoped
@Provider
@PreMatching
@Priority(900)
public class OriginReferrerFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(OriginReferrerFilter.class);

	private static final List<String> NO_CONTENT_PATHS = Arrays.asList(new String[] { "/favicon.ico" });

	@Inject
	ConfigService config;

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		UriInfo uriInfo = requestContext.getUriInfo();
		String pathInfo = uriInfo.getPath();

		if (NO_CONTENT_PATHS.contains(pathInfo) || "OPTIONS".equals(requestContext.getMethod())) {

			throw new NoContentException(pathInfo);
		}

		validateOriginAndRefererHeader(requestContext);
	}

	private void validateOriginAndRefererHeader(final ContainerRequestContext requestContext) throws IOException {

		final String origin = requestContext.getHeaderString("Origin");
		final String referer = requestContext.getHeaderString("Referer");

		LOG.debug("Origin = [{}], Referer=[{}]", origin, referer);

		if (StringUtils.isBlank(origin) && StringUtils.isBlank(referer)) {

			final String details = "Header Origin UND Referer fehlen";

			if (config.isBlockOnMissingOriginReferer()) {

				logErrorAndThrow(details, requestContext);
			}
		}

		if (!StringUtils.isBlank(origin)) {

			checkHeaderTarget(origin, requestContext);
		}

		if (!StringUtils.isBlank(referer)) {

			checkHeaderTarget(referer, requestContext);
		}
	}

	private void checkHeaderTarget(final String headerValue, final ContainerRequestContext requestContext) throws IOException {

		final String extractedValue = CommonHttpUtils.extractOrigin(headerValue);

		if (extractedValue == null) {

			return;
		}

		final String targetOrigin = config.getTargetOrigin();

		if (targetOrigin != null) {

			List<String> allowedOrigins = Arrays.asList(targetOrigin.split(","));

			if (!allowedOrigins.contains(extractedValue)) {

				final String details = "targetOrigin != extractedOrigin: [targetOrigin=" + targetOrigin
					+ ", extractedOrigin="
					+ extractedValue + "]";
				logErrorAndThrow(details, requestContext);
			}
		}
	}

	private void logErrorAndThrow(final String details, final ContainerRequestContext requestContext) throws IOException {

		final String dump = CommonHttpUtils.getRequestInfos(requestContext);
		LOG.warn("Possible CSRF-Attack: {} - {}", details, dump);
		throw new AuthException("Possible CSRF-Attack");
	}

}
