package grisu.frontend.view.cli;

import grisu.frontend.control.login.LoginManagerNew;
import grith.jgrith.cred.GridCliParameters;

import com.beust.jcommander.Parameter;

public class GrisuCliParameters extends GridCliParameters {

	@Parameter(names = { "-b", "--backend" }, description = "backend to login to")
	private String backend = LoginManagerNew.DEFAULT_BACKEND;

	public String getBackend() {
		return backend;
	}

}
