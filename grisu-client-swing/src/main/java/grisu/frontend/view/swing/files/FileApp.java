package grisu.frontend.view.swing.files;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.control.login.LoginParams;
import grisu.frontend.view.swing.files.preview.FileListWithPreviewPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;


public class FileApp {

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					final String username = args[0];
					final char[] password = args[1].toCharArray();

					final LoginParams loginParams = new LoginParams(
					// "http://localhost:8080/grisu-ws/services/grisu",
					// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
					// "http://localhost:8080/enunciate-backend/soap/GrisuService",
							"Local",
							// "LOCAL_WS",
							username, password);

					ServiceInterface si = null;
					// si = LoginManager.login(null, password, username, "VPAC",
					// loginParams);
					si = LoginManager
							.login(null, null, null, null, loginParams);

					final File home = new File(System.getProperty("user.home"));

					final FileApp window = new FileApp(si, home.toURI()
							.toString());
					window.frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frame;

	private FileListPanelPlus fileListPanelPlus;
	private FileListWithPreviewPanel flwpp;
	private final ServiceInterface si;

	private final String startUrl;

	/**
	 * Create the application.
	 */
	public FileApp(ServiceInterface si, String startUrl) {
		this.si = si;
		this.startUrl = startUrl;

		initialize();
	}

	private FileListPanelPlus getFileListPanelPlus() {
		if (fileListPanelPlus == null) {
			fileListPanelPlus = new FileListPanelPlus(si, startUrl, true, true);
		}
		return fileListPanelPlus;
	}

	private FileListWithPreviewPanel getFileListWithPreviewPanel() {
		if (flwpp == null) {
			flwpp = new FileListWithPreviewPanel(si, null, startUrl, true,
					false, false, false, true);
		}
		return flwpp;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		// frame.getContentPane().add(getFileListPanelPlus(),
		// BorderLayout.CENTER);
		frame.getContentPane().add(getFileListWithPreviewPanel(),
				BorderLayout.CENTER);
	}
}
