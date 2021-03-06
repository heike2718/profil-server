// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * User
 */
public class User {

	private String loginName;

	private String email;

	private String vorname;

	private String nachname;

	/**
	 *
	 */
	public User() {

	}

	public String getLoginName() {

		return loginName;
	}

	public void setLoginName(final String loginName) {

		this.loginName = loginName;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(final String email) {

		this.email = email;
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

	private boolean hasName() {

		return StringUtils.isNotBlank(vorname) || StringUtils.isNotBlank(nachname);
	}

	public String getFullName() {

		if (!hasName()) {

			return "";
		}
		return vorname + " " + nachname;
	}

	@Override
	public String toString() {

		return "User [vorname=" + vorname + ", nachname=" + nachname + ", loginName=" + loginName + ", email=" + email + "]";
	}

}
