package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;

public class FileApp {

	private JFrame frame;
	private FileListPanelPlus fileListPanelPlus;
	
	private ServiceInterface si;
	private String startUrl;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					String username = args[0];
					char[] password = args[1].toCharArray();

					LoginParams loginParams = new LoginParams(
					// "http://localhost:8080/grisu-ws/services/grisu",
//							 "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
//							"http://localhost:8080/enunciate-backend/soap/GrisuService",
					"Local", 
//					"LOCAL_WS",
					username, password);
					
					
					ServiceInterface si = null;
//					si = LoginManager.login(null, password, username, "VPAC", loginParams);
					si = LoginManager.login(null, null, null, null, loginParams);
					
					File home = new File(System.getProperty("user.home"));
					
					FileApp window = new FileApp(si, home.toURI().toString());
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileApp(ServiceInterface si, String startUrl) {
		this.si = si;
		this.startUrl = startUrl;
		
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
		frame.getContentPane().add(getFileListPanelPlus(), BorderLayout.CENTER);
	}

	
	
	private FileListPanelPlus getFileListPanelPlus() {
		if (fileListPanelPlus == null) {
			fileListPanelPlus = new FileListPanelPlus(si, (String) null, startUrl);
		}
		return fileListPanelPlus;
	}
}
