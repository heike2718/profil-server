// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server.utils;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * SessionUtils
 */
public final class SessionUtils {

	public static final String NAME_SESSIONID_COOKIE = "_SESSIONID";

	/**
	 *
	 */
	private SessionUtils() {

	}

	public static NewCookie createSessionInvalidatedCookie() {

		long dateInThePast = CommonTimeUtils.now().minus(10, ChronoUnit.YEARS).toEpochSecond(ZoneOffset.UTC);

		return new NewCookie(NAME_SESSIONID_COOKIE, null, null, null, Cookie.DEFAULT_VERSION, null, 0,
			new Date(dateInThePast), true, true);

	}

}
