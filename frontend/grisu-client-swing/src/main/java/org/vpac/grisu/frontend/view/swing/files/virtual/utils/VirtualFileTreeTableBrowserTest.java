package org.vpac.grisu.frontend.view.swing.files.virtual.utils;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.view.swing.files.virtual.GridFileManagementPanel;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

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
			GridFile f = new GridFile(
					"grid://groups/ARCS/BeSTGRID/Drug_discovery//");

			f.setIsVirtual(true);
			f.setPath(f.getUrl());
			GridFile l = GrisuRegistryManager.getDefault(si).getFileManager()
					.getLocalRoot();
			List<GridFile> files = new LinkedList<GridFile>();
			files.add(f);
			files.add(l);
			panel = new GridFileManagementPanel(si, files, files);
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
