package grisu.frontend.view.swing.files.virtual.utils;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;


public class VirtualFileBrowserTest {

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
					VirtualFileBrowserTest window = new VirtualFileBrowserTest(
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
	private VirtualFileSystemTreePanel groupFileBrowserPanel;

	/**
	 * Create the application.
	 */
	public VirtualFileBrowserTest(ServiceInterface si) {
		this.si = si;

		initialize();
	}

	private VirtualFileSystemTreePanel getGroupFileBrowserPanel() {
		if (groupFileBrowserPanel == null) {
			groupFileBrowserPanel = new VirtualFileSystemTreePanel(si);
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
