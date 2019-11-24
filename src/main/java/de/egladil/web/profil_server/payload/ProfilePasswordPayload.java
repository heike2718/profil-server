// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import javax.validation.constraints.NotNull;

import de.egladil.web.commons_validation.SecUtils;
import de.egladil.web.commons_validation.annotations.Passwort;
import de.egladil.web.commons_validation.annotations.ValidPasswords;
import de.egladil.web.commons_validation.payload.TwoPasswords;

/**
 * ProfilePasswordPayload
 */
public class ProfilePasswordPayload {

	@NotNull
	@Passwort
	private String passwort;

	@NotNull
	@ValidPasswords
	private TwoPasswords twoPasswords;

	/**
	 * Entfernt alle sensiblen Infos: also password und passwordWdh.
	 */
	public void clean() {

		if (twoPasswords != null) {

			twoPasswords.clean();
			twoPasswords = null;
		}

		if (passwort != null) {

			passwort = SecUtils.wipe(passwort);
			passwort = null;
		}
	}

	public TwoPasswords getTwoPasswords() {

		return twoPasswords;
	}

	public void setTwoPasswords(final TwoPasswords twoPasswords) {

		this.twoPasswords = twoPasswords;
	}

	public String getPasswort() {

		return passwort;
	}

	public void setPasswort(final String passwort) {

		this.passwort = passwort;
	}

}
