// =====================================================
// Project: authprovider
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.endpoints;

import java.security.Principal;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.profil_server.dao.ResourceOwnerDao;
import de.egladil.web.profil_server.domain.ResourceOwner;
import de.egladil.web.profil_server.domain.UserSession;
import de.egladil.web.profil_server.error.AuthException;
import de.egladil.web.profil_server.payload.StringPayload;

/**
 * ValidationResource
 */
@RequestScoped
@Path("/validators")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ValidationResource {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationResource.class);

	@Inject
	ResourceOwnerDao resourceOwnerDao;

	@Context
	SecurityContext securityContext;

	@POST
	@Path("loginname")
	public Response checkLoginnameExists(final StringPayload loginName) {

		UserSession userSession = this.getUserSession();

		Optional<ResourceOwner> optUserWithLoginName = this.resourceOwnerDao.findByLoginNameButNotWithUUID(loginName.getInput(),
			userSession.getUuid());

		if (optUserWithLoginName.isPresent()) {

			return Response
				.ok(ResponsePayload.messageOnly(MessagePayload.error("Diesen Loginnamen gibt es schon."))).build();
		}
		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("keine Einwände"))).build();

	}

	@POST
	@Path("email")
	public Response checkEmailExists(final StringPayload email) {

		UserSession userSession = this.getUserSession();

		Optional<ResourceOwner> optUserWithEmail = this.resourceOwnerDao.findByEmailButNotWithUUID(email.getInput(),
			userSession.getUuid());

		if (optUserWithEmail.isPresent()) {

			return Response
				.ok(ResponsePayload.messageOnly(MessagePayload.error("Diese Mailadresse gibt es schon."))).build();
		}
		return Response.ok(ResponsePayload.messageOnly(MessagePayload.info("keine Einwände"))).build();

	}

	private UserSession getUserSession() {

		Principal principal = securityContext.getUserPrincipal();

		if (principal != null) {

			return (UserSession) principal;
		}

		LOG.error("keine UserSession für Principal vorhanden");
		throw new AuthException("Keine Berechtigung");
	}
}
