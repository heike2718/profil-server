// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.profil_server.dao.ResourceOwnerDao;
import de.egladil.web.profil_server.domain.ResourceOwner;
import de.egladil.web.profil_server.error.ProfilserverRuntimeException;

/**
 * ResourceOwnerDaoImpl
 */
@RequestScoped
public class ResourceOwnerDaoImpl implements ResourceOwnerDao {

	@Inject
	EntityManager entityManager;

	@Override
	public Optional<ResourceOwner> findByUuid(final String uuid) {

		if (uuid == null) {

			throw new IllegalArgumentException("uuid null");
		}

		if (entityManager == null) {

			System.err.println("entityManager ist null :/");
			throw new ProfilserverRuntimeException("entityManager ist null");
		}

		List<ResourceOwner> trefferliste = entityManager.createNamedQuery(ResourceOwner.FIND_BY_UUID, ResourceOwner.class)
			.setParameter("uuid", uuid)
			.getResultList();

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		if (trefferliste.size() > 1) {

			throw new ProfilserverRuntimeException("Mehr als ein Eintrag mit UUID='" + uuid + "' in tabelle USERS");
		}
		ResourceOwner resourceOwner = trefferliste.get(0);

		System.out.println("ResourceOwnerDaoImpl: ressourceOwner gefunden: " + resourceOwner.toString());

		return Optional.of(resourceOwner);
	}
}
