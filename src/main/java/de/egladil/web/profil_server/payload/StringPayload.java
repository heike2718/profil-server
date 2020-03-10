// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * StringPayload
 */
public class StringPayload {

	@NotBlank
	@Size(max = 255)
	private String input;

	/**
	 *
	 */
	public StringPayload() {

	}

	public String getInput() {

		return input;
	}
}
