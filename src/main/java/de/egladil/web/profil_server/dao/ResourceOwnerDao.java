// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.dao;

import java.util.Optional;

import de.egladil.web.profil_server.domain.ResourceOwner;

/**
 * ResourceOwnerDao
 */
public interface ResourceOwnerDao {

	Optional<ResourceOwner> findByUuid(String uuid);

}
