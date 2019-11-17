// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.endpoints;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.ValidationDelegate;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.OAuthClientCredentials;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.profil_server.domain.AuthenticatedUser;
import de.egladil.web.profil_server.domain.User;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.error.AuthException;
import de.egladil.web.profil_server.error.LogmessagePrefixes;
import de.egladil.web.profil_server.error.ProfilserverRuntimeException;
import de.egladil.web.profil_server.payload.ChangeProfileDataPayload;
import de.egladil.web.profil_server.payload.ChangeProfilePasswordPayload;
import de.egladil.web.profil_server.payload.ProfileDataPayload;
import de.egladil.web.profil_server.payload.ProfilePasswordPayload;
import de.egladil.web.profil_server.restclient.ProfileRestClient;
import de.egladil.web.profil_server.service.AuthenticatedUserService;
import de.egladil.web.profil_server.service.SessionService;

/**
 * ProfilResource
 */
@RequestScoped
@Path("profiles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfilResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProfilResource.class);

	@ConfigProperty(name = "auth.client-id")
	String clientId;

	@ConfigProperty(name = "auth.client-secret")
	String clientSecret;

	@Inject
	AuthenticatedUserService authentiatedUserService;

	@Inject
	@RestClient
	ProfileRestClient profileRestClient;

	@Inject
	SessionService sessionService;

	private ValidationDelegate validationDelegate = new ValidationDelegate();

	@PUT
	@Path("/profile/{idRef}/password")
	@PermitAll
	public Response changePassword(@PathParam(value = "idRef") final String idRef, final ProfilePasswordPayload payload) {

		Optional<UserSession> optUserSession = sessionService.findSessionByIdReference(idRef);

		if (!optUserSession.isPresent()) {

			LOG.error("keine UserSession mit idReference={} vorhanden", idRef);
			throw new AuthException("keine Berechtigung");
		}

		UserSession userSession = optUserSession.get();
		NewCookie sessionCookie = authentiatedUserService.createSessionCookie(userSession.getSessionId());

		String expectedNonce = UUID.randomUUID().toString();
		OAuthClientCredentials clientCredentials = OAuthClientCredentials.create(clientId, clientSecret, expectedNonce);

		try {

			validationDelegate.check(payload, ProfilePasswordPayload.class);

			ChangeProfilePasswordPayload changePasswordPayload = new ChangeProfilePasswordPayload(clientCredentials, payload);

			Response authProviderResponse = profileRestClient.changePassword(userSession.getUuid(), changePasswordPayload);

			ResponsePayload responsePayload = authProviderResponse.readEntity(ResponsePayload.class);
			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String nonce = dataMap.get("nonce");

			if (!expectedNonce.equals(nonce)) {

				LOG.warn(LogmessagePrefixes.BOT + "angefragter Entdpoint hat das nonce geändert: expected={}, actual={}",
					expectedNonce, nonce);

				throw new ProfilserverRuntimeException("Der authprovider konnte nicht erreich werden");

			}

			if (authProviderResponse.getStatus() == 200) {

				AuthenticatedUser responseData = authentiatedUserService.createAuthenticatedUser(userSession, null);

				ResponsePayload mappedResponsePayload = new ResponsePayload(responsePayload.getMessage(), responseData);
				return Response.ok(mappedResponsePayload).cookie(sessionCookie).build();
			} else {

				LOG.error("authprovider antwortete mit dem Status {}", authProviderResponse.getStatus());
				throw new ProfilserverRuntimeException("Die Daten konnten wegen eines Serverfehlers nicht geändert werden.");

			}

		} catch (ProfilserverRuntimeException e) {

			AuthenticatedUser authenticatedUser = authentiatedUserService.createAuthenticatedUser(userSession, null);
			return Response.serverError().entity(new ResponsePayload(MessagePayload.error(e.getMessage()), authenticatedUser))
				.cookie(sessionCookie).build();

		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			AuthenticatedUser authenticatedUser = authentiatedUserService.createAuthenticatedUser(userSession, null);
			return Response.serverError()
				.entity(new ResponsePayload(
					MessagePayload.error("Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de"),
					authenticatedUser))
				.cookie(sessionCookie).build();

		} finally {

			clientCredentials.clean();
			payload.clean();
		}
	}

	/**
	 * @param  idRef
	 * @param  payload
	 * @return         ResponsePayload mit AuthenticatedUser als data.
	 */
	@PUT
	@Path("/profile/{idRef}/data")
	@PermitAll
	public Response changeData(@PathParam(value = "idRef") final String idRef, final ProfileDataPayload payload) {

		Optional<UserSession> optUserSession = sessionService.findSessionByIdReference(idRef);

		if (!optUserSession.isPresent()) {

			LOG.error("keine UserSession mit idReference={} vorhanden", idRef);
			throw new AuthException("keine Berechtigung");
		}

		UserSession userSession = optUserSession.get();
		NewCookie sessionCookie = authentiatedUserService.createSessionCookie(userSession.getSessionId());

		String expectedNonce = UUID.randomUUID().toString();
		OAuthClientCredentials clientCredentials = OAuthClientCredentials.create(clientId, clientSecret, expectedNonce);

		try {

			validationDelegate.check(payload, ProfileDataPayload.class);

			ChangeProfileDataPayload changePasswordPayload = new ChangeProfileDataPayload(clientCredentials, payload);

			Response authProviderResponse = profileRestClient.changeData(userSession.getUuid(), changePasswordPayload);

			LOG.debug("Response-Status={}", authProviderResponse.getStatus());

			ResponsePayload responsePayload = authProviderResponse.readEntity(ResponsePayload.class);
			@SuppressWarnings("unchecked")
			Map<String, String> dataMap = (Map<String, String>) responsePayload.getData();
			String nonce = dataMap.get("nonce");

			if (!expectedNonce.equals(nonce)) {

				LOG.warn(LogmessagePrefixes.BOT + "angefragter Entdpoint hat das nonce geändert: expected={}, actual={}",
					expectedNonce, nonce);

				throw new ProfilserverRuntimeException("Der authprovider konnte nicht erreich werden");

			}

			if (authProviderResponse.getStatus() == 200) {

				User user = new User();
				user.setEmail(dataMap.get("email"));
				user.setLoginName(dataMap.get("loginName"));
				user.setNachname(dataMap.get("nachname"));
				user.setVorname(dataMap.get("vorname"));

				AuthenticatedUser responseData = authentiatedUserService.createAuthenticatedUser(userSession, user);

				ResponsePayload mappedResponsePayload = new ResponsePayload(responsePayload.getMessage(), responseData);
				return Response.ok(mappedResponsePayload).cookie(sessionCookie).build();

			} else {

				LOG.error("authprovider antwortete mit dem Status {}", authProviderResponse.getStatus());
				throw new ProfilserverRuntimeException("Die Daten konnten wegen eines Serverfehlers nicht geändert werden.");

			}

		} catch (ProfilserverRuntimeException e) {

			AuthenticatedUser authenticatedUser = authentiatedUserService.createAuthenticatedUser(userSession, null);
			return Response.serverError().entity(new ResponsePayload(MessagePayload.error(e.getMessage()), authenticatedUser))
				.cookie(sessionCookie).build();

		} catch (Exception e) {

			LOG.error(e.getMessage(), e);

			// Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de

			AuthenticatedUser authenticatedUser = authentiatedUserService.createAuthenticatedUser(userSession, null);
			return Response.serverError()
				.entity(new ResponsePayload(
					MessagePayload.error("Es ist ein Fehler aufgetreten. Bitte senden Sie eine Mail an info@egladil.de"),
					authenticatedUser))
				.cookie(sessionCookie).build();

		} finally {

			clientCredentials.clean();
		}
	}
}
