package grisu.frontend.view.cli;

import grith.gridsession.GridSessionCred;
import grith.jgrith.cred.Cred;

public class TestCliClient {

	public static void main(String[] args) {

		GridLoginParameters.useGridSession = true;

		Cred cred = new GridSessionCred();
		System.out.println(cred.isValid());

		// GridLoginParameters glp = GridLoginParameters
		// .createFromCommandlineArgs(args);
		//
		// glp.setCallback(new CliCallback());
		//
		// glp.populate();
		// Cred c = glp.createCredential();
		//
		// System.out.println(c.getDN());

	}

}
