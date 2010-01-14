package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXFrame;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.events.ApplicationEventListener;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

import au.org.arcs.auth.shibboleth.Shibboleth;

public class GrisuApplicationWindow {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		Shibboleth.initDefaultSecurityProvider();

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

		new ApplicationEventListener();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrisuApplicationWindow window = new GrisuApplicationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private LoginPanel lp;

	private JXFrame frame;

	/**
	 * Create the application.
	 */
	public GrisuApplicationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JXFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		GrisuMainPanel mainPanel = new GrisuMainPanel();
		//		LoginPanel lp = new LoginPanel(mainPanel, true);
		LoginPanel lp = new LoginPanel(mainPanel, false);
		frame.getContentPane().add(lp, BorderLayout.CENTER);
	}

	public void setServiceInterface(ServiceInterface si) {

		if ( lp == null ) {
			throw new IllegalStateException("LoginPanel not initialized.");
		}

		if ( si == null ) {
			throw new NullPointerException("ServiceInterface can't be null");
		}

		lp.setServiceInterface(si);
	}

}
