package grisu.frontend.view.swing.files.virtual.utils;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.swing.files.virtual.GridFileManagementPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;


public class VirtualFileTreeTableBrowserTest {

	/**
	 * Launch the application.
	 * 
	 * @throws LoginException
	 */
	public static void main(String[] args) throws LoginException {

		// final ServiceInterface si = LoginManager.loginCommandline("LOCAL");
		final ServiceInterface si = LoginManager.loginCommandline("LOCAL_WS");

		// final ServiceInterface si = LoginManager
		// .loginCommandline("BeSTGRID-DEV");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VirtualFileTreeTableBrowserTest window = new VirtualFileTreeTableBrowserTest(
							si);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// try {
		// GroupFileBrowserTest window = new GroupFileBrowserTest(si,
		// false);
		// window.frame.setVisible(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}

	private final ServiceInterface si;

	private JFrame frame;
	// private VirtualFileSystemTreeTablePanel groupFileBrowserPanel;
	private GridFileManagementPanel panel;

	/**
	 * Create the application.
	 */
	public VirtualFileTreeTableBrowserTest(ServiceInterface si) {
		this.si = si;

		initialize();
	}

	private GridFileManagementPanel getGroupFileBrowserPanel() {
		if (panel == null) {
			panel = new GridFileManagementPanel(si, null, null);
		}
		return panel;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(getGroupFileBrowserPanel(),
				BorderLayout.CENTER);
	}
}
