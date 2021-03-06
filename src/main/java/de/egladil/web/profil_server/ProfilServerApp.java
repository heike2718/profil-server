// =====================================================
// Project: profil-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.profil_server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * ProfilServerApp
 */
@ApplicationPath("/profil-api")
public class ProfilServerApp extends Application {

	public static final String CLIENT_COOKIE_PREFIX = "PRF";

	public static final String STAGE_DEV = "dev";

}
