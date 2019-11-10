// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UserSession
 */
public class UserSession {

	@JsonIgnore
	private String sessionId;

	@JsonIgnore
	private String uuid;

	private String roles;

	private String fullName;

	private long expiresAt;

	public static UserSession create(final String sessionId, final String roles, final String fullName) {

		UserSession result = new UserSession();
		result.sessionId = sessionId;
		result.roles = roles;
		result.fullName = fullName;
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

	@Override
	public String toString() {

		return "UserSession [roles=" + roles + ", fullName=" + fullName + ", expiresAt=" + expiresAt + "]";
	}

}
