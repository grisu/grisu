/* Copyright 2006 VPAC
 * 
 * This file is part of security_test.
 * Grix is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.

 * Grix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Grix; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.vpac.grisu.view.ws.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/**
 * Not needed anymore.
 * 
 * @author Markus Binsteiner
 *
 */
public class PasswordHandler  implements CallbackHandler {
	private Map passwords = new HashMap();

	public PasswordHandler() {
//		passwords.put("server", "aliaspass");
//        passwords.put("client","changeit_client");
//        passwords.put("markus_new","nixenixe25");
//        passwords.put("markus_grix", "nixenixe25");

	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
//		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
//		String id = pc.getIdentifer();
//		pc.setPassword((String) passwords.get(id));

	}
}
