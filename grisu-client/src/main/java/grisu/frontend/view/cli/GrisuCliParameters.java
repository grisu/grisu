package grisu.frontend.view.cli;

import grisu.frontend.control.login.LoginManager;
import grith.jgrith.cred.GridCliParameters;

import com.beust.jcommander.Parameter;

public class GrisuCliParameters extends GridCliParameters {

	@Parameter(names = { "-b", "--backend" }, description = "backend to login to")
	private String backend = LoginManager.DEFAULT_BACKEND;

	public String getBackend() {
		return backend;
	}

}
