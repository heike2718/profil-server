// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.service;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.error.ProfilserverRuntimeException;
import de.egladil.web.profil_server.restclient.ProfileRestClient;

/**
 * UserService
 */
@RequestScoped
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@ConfigProperty(name = "auth.client-secret")
	String clientSecret;

	@Inject
	@RestClient
	ProfileRestClient profileRestClient;

	/**
	 * REST-API- Aufruf, um die Userdaten zu holen.
	 *
	 * @param  uuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public User getUser(final String uuid) {

		OAuthClientCredentials credentials = OAuthClientCredentials.create(clientId,
			clientSecret, null);

		try {

			Response response = profileRestClient.getUserProfile(uuid, credentials);

			LOG.debug("UserService === (3) ===");

			ResponsePayload payload = response.readEntity(ResponsePayload.class);
			MessagePayload messagePayload = payload.getMessage();

			if (messagePayload.isOk()) {

				Map<String, String> data = (Map<String, String>) payload.getData();

				User user = new User();
				user.setEmail(data.get("email"));
				user.setLoginName(data.get("loginName"));
				user.setNachname(data.get("nachname"));
				user.setVorname(data.get("vorname"));

				return user;
			}

			return null;
		} catch (WebApplicationException e) {

			Response response = e.getResponse();
			int status = response.getStatus();

			if (status == 401) {

				LOG.error("Authentisierungsfehler für Client {} gegenüber dem authprovider", StringUtils.abbreviate(clientId, 11));
				throw new ProfilserverRuntimeException("Authentisierungsfehler für Client");
			}

			LOG.error("Statuscode {} beim Holen des Users", status);

			throw new ProfilserverRuntimeException("Unerwarteter Response-Status beim Holen des Users.");

		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			throw new ProfilserverRuntimeException("Fehler bei Anfrage des authproviders: " + e.getMessage(), e);

		} finally {

			credentials.clean();
		}
	}
}
