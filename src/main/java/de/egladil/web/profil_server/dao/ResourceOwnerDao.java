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

	/**
	 * Suche nach resourceOwner.email.
	 *
	 * @param  email
	 *               String
	 * @param  uuid
	 *               String die UUID des anfragenden Users. Dieser User soll nicht geunden werden!
	 * @return       Optional
	 */
	Optional<ResourceOwner> findByEmailButNotWithUUID(String email, String uuid);

	/**
	 * Suche nach resourceOwner.loginName.
	 *
	 * @param  email
	 *               String
	 * @param  uuid
	 *               String die UUID des anfragenden Users. Dieser User soll nicht geunden werden!
	 * @return       Optional
	 */
	Optional<ResourceOwner> findByLoginNameButNotWithUUID(String loginName, String uuid);
}
