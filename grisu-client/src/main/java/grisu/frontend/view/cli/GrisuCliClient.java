package grisu.frontend.view.cli;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManagerNew;
import grisu.jcommons.configuration.CommonGridProperties;
import grith.gridsession.GridClient;
import grith.gridsession.GridSessionCred;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;
import grith.jgrith.cred.callbacks.CliCallback;

import java.util.List;

public class GrisuCliClient extends GridClient {



	public static void main(String[] args) {

		// System.setProperty(Property.USE_GRID_SESSION.toString(), "false");
		GrisuCliClient gcc = new GrisuCliClient(args);


		execute(gcc, false);

	}

	protected final String[] args;
	private GridLoginParameters loginParams = null;
	private Cred cred = null;

	private ServiceInterface si;

	public GrisuCliClient(String[] args) {
		super();
		LoginManagerNew.initEnvironment();
		this.args = args;
	}

	public Cred getCredential() {
		if ((cred == null) || getLoginParameters().isNologin()) {

			if (CommonGridProperties.getDefault().useGridSession()) {

				cred = new GridSessionCred();
				if (!cred.isValid()) {

					if (!getLoginParameters().validConfig()) {
						myLogger.debug("Trying to retieve remaining login details.");
						getLoginParameters().setCallback(new CliCallback());
						getLoginParameters().populate();
					}

					cred.init(getLoginParameters().getCredProperties());

				}
			} else {
				if (!getLoginParameters().validConfig()) {
					myLogger.debug("Trying to retieve remaining login details.");
					getLoginParameters().setCallback(new CliCallback());
					getLoginParameters().populate();
				}
				cred = AbstractCred.loadFromConfig(getLoginParameters()
						.getCredProperties());
			}
		}
		return cred;
	}

	public GridLoginParameters getLoginParameters() {
		if (loginParams == null) {
			loginParams = GridLoginParameters.createFromCommandlineArgs(args);

			if (loginParams.isLogout()) {
				getCredential().destroy();
				System.exit(0);
			}

		}
		return loginParams;
	}

	public List<String> getOtherParameters() {
		return getLoginParameters().getOtherParameters();
	}

	public ServiceInterface getServiceInterface() throws LoginException {
		if ((si == null) || getLoginParameters().isNologin()) {
			si = LoginManagerNew.login(getLoginParameters().getBackend(),
					getCredential(), true);
		}
		return si;
	}

	@Override
	public void run() {


		System.out.println("Example grisu client.");
		ServiceInterface si = null;
		try {
			si = getServiceInterface();
		} catch (LoginException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("Grid identity: " + si.getDN());
		System.out.println("Backend info:");
		System.out.println(si.getInterfaceInfo(null));

	}

}
