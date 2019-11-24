// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.LoginName;
import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * ChangeProfileDataPayload
 */
public class ProfileDataPayload {

	@NotNull
	@Email
	@Size(min = 1, max = 255)
	private String email;

	@NotNull
	@LoginName
	@Size(max = 255)
	private String loginName;

	@StringLatin
	@Size(min = 1, max = 100)
	@Column(name = "VORNAME")
	private String vorname;

	@StringLatin
	@Size(min = 1, max = 100)
	@Column(name = "NACHNAME")
	private String nachname;

	public String getEmail() {

		return email;
	}

	public void setEmail(final String email) {

		this.email = email;
	}

	public String getLoginName() {

		return loginName;
	}

	public void setLoginName(final String loginName) {

		this.loginName = loginName;
	}

	public String getVorname() {

		return vorname;
	}

	public void setVorname(final String vorname) {

		this.vorname = vorname;
	}

	public String getNachname() {

		return nachname;
	}

	public void setNachname(final String nachname) {

		this.nachname = nachname;
	}

	@Override
	public String toString() {

		return "ChangeProfileDataPayload [email=" + email + ", loginName=" + loginName + ", vorname=" + vorname + ", nachname="
			+ nachname
			+ "]";
	}

}
