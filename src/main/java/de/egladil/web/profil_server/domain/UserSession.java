// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.domain;

import java.io.Serializable;
import java.security.Principal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UserSession
 */
public class UserSession implements Principal, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String sessionId;

	@JsonIgnore
	private String uuid;

	private String idReference;

	private String roles;

	private String fullName;

	private long expiresAt;

	public static UserSession create(final String sessionId, final String roles, final String fullName, final String idReference) {

		UserSession result = new UserSession();
		result.sessionId = sessionId;
		result.roles = roles;
		result.fullName = fullName;
		result.idReference = idReference;
		return result;
	}

	public String getSessionId() {

		return sessionId;
	}

	public String getRoles() {

		return roles;
	}

	public String getFullName() {

		return fullName;
	}

	public long getExpiresAt() {

		return expiresAt;
	}

	public void setExpiresAt(final long expiresAt) {

		this.expiresAt = expiresAt;
	}

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public void removeSessionId() {

		this.sessionId = null;
	}

	@Override
	public String toString() {

		return "UserSession [roles=" + roles + ", fullName=" + fullName + ", expiresAt=" + expiresAt + ", uuid="
			+ uuid.substring(0, 8) + "]";
	}

	public String getIdReference() {

		return idReference;
	}

	@Override
	public String getName() {

		return uuid;
	}

	public void clearSessionId() {

		this.sessionId = null;
	}

}
