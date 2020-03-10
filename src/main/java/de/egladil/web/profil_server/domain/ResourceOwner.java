// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * ResourceOwner
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
	@NamedQuery(name = "FIND_BY_UUID", query = "select o from ResourceOwner o where o.uuid = :uuid"),
	@NamedQuery(
		name = "FIND_OTHER_BY_EMAIL", query = "select o from ResourceOwner o where o.uuid != :uuid and lower(o.email) = :email"),
	@NamedQuery(
		name = "FIND_OTHER_BY_LOGINNAME",
		query = "select o from ResourceOwner o where o.uuid != :uuid and o.loginName = :loginName"),
})
public class ResourceOwner {

	public static final String FIND_BY_UUID = "FIND_BY_UUID";

	public static final String FIND_OTHER_BY_EMAIL = "FIND_OTHER_BY_EMAIL";

	public static final String FIND_OTHER_BY_LOGINNAME = "FIND_OTHER_BY_LOGINNAME";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@JsonIgnore
	private Long id;

	@UuidString
	@NotBlank
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@NotBlank
	@StringLatin
	@Size(min = 1, max = 255)
	@Column(name = "LOGINNAME")
	private String loginName;

	@NotBlank
	@Email
	@Size(min = 1, max = 255)
	@Column(name = "EMAIL")
	private String email;

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@Override
	public String toString() {

		return "ResourceOwner [id=" + id + ", uuid=" + uuid + "]";
	}

}
