package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;

public class VirtualFileTreeTableBrowserTest {

	/**
	 * Launch the application.
	 * 
	 * @throws LoginException
	 */
	public static void main(String[] args) throws LoginException {

		final ServiceInterface si = LoginManager.loginCommandline("LOCAL");
		// final ServiceInterface si = LoginManager.loginCommandline("Local");

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
	private VirtualFileSystemTreeTablePanel groupFileBrowserPanel;

	/**
	 * Create the application.
	 */
	public VirtualFileTreeTableBrowserTest(ServiceInterface si) {
		this.si = si;

		initialize();
	}

	private VirtualFileSystemTreeTablePanel getGroupFileBrowserPanel() {
		if (groupFileBrowserPanel == null) {
			groupFileBrowserPanel = new VirtualFileSystemTreeTablePanel(si);
		}
		return groupFileBrowserPanel;
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
