package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.view.swing.files.GrisuTestSwingClientPanel;
import org.vpac.grisu.frontend.view.swing.login.GrisuSwingClient;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class BatchJobMonitoringApp {

	/**
	 * Launch the application.
	 * 
	 * @throws LoginException
	 */
	public static void main(final String[] args) throws LoginException {

		// String username = args[0];
		// char[] password = args[1].toCharArray();
		//
		// LoginParams loginParams = new LoginParams(
		// // "http://localhost:8080/grisu-ws/services/grisu",
		// // "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
		// // "http://localhost:8080/enunciate-backend/soap/GrisuService",
		// "Local",
		// // "LOCAL_WS",
		// username, password);

		// final ServiceInterface si;
		// si = LoginManager.login(null, password, username, "VPAC",
		// loginParams);
		// si = LoginManager.login(null, null, null, null, loginParams);

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					BatchJobMonitoringApp window = new BatchJobMonitoringApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frame;

	// private final ServiceInterface si;

	/**
	 * Create the application.
	 */
	public BatchJobMonitoringApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		// frame.add(new BatchJobTabbedPane(si, null), BorderLayout.CENTER);
		GrisuSwingClient client = new GrisuTestSwingClientPanel();
		frame.add(new LoginPanel(client), BorderLayout.CENTER);
	}

}
