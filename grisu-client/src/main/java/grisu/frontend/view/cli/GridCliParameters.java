package grisu.frontend.view.cli;

import grisu.frontend.control.login.LoginManagerNew;
import grisu.jcommons.constants.GridEnvironment;
import grith.jgrith.utils.CliLogin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.Parameter;

public class GridCliParameters {

	@Parameter(description = "Other params")
	private List<String> other = new ArrayList<String>();

	@Parameter(names = { "-b", "--backend" }, description = "backend to login to")
	private String backend = LoginManagerNew.DEFAULT_BACKEND;

	@Parameter(names = "--nologin", description = "skip logging in")
	private boolean nologin = false;

	@Parameter(names = "--logout", description = "destroys a possible grid session and exits straight away")
	private boolean logout = false;

	@Parameter(names = { "-u", "--username" }, description = "institution or myproxy username")
	private String username;

	@Parameter(names = { "-i", "--institution" }, description = "institution name")
	private String institution;

	@Parameter(names = { "-m", "--myproxy_host" }, description = "myproxy host to use")
	private String myproxy_host = GridEnvironment.getDefaultMyProxyServer();

	@Parameter(names = { "-x", "--x509" }, description = "x509 certificate login")
	private boolean useX509;

	private char[] password;

	public String getBackend() {
		return backend;
	}

	public String getInstitution() {
		return institution;
	}

	public String getMyproxy_host() {
		return myproxy_host;
	}

	public List<String> getOtherParams() {
		return other;
	}

	public char[] getPassword() {

		if (password == null) {
			return CliLogin.askPassword("Please enter the password");
		}
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isLogout() {
		return logout;
	}

	public boolean isNologin() {
		return nologin;
	}

	public boolean useIdPLogin() {
		if (!useX509
				&& StringUtils.isNotBlank(getInstitution())
				&& StringUtils.isNotBlank(username)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean useMyProxyLogin() {
		if (!useX509
				&& StringUtils.isBlank(institution)
				&& StringUtils.isNotBlank(username)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean useX509Login() {
		if (useX509) {
			return true;
		} else {
			return false;
		}
	}

	public boolean valid() {
		if (((!useIdPLogin()) && (!useMyProxyLogin()) && (!useX509Login()))
				|| (useIdPLogin() && useMyProxyLogin())
				|| (useIdPLogin() && useX509Login())
				|| (useMyProxyLogin() && useX509Login())) {
			return false;
		} else {
			return true;
		}
	}


}
