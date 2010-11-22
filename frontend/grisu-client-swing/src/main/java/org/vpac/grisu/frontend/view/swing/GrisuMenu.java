package org.vpac.grisu.frontend.view.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.GrisuVersion;
import org.vpac.grisu.control.ServiceInterface;

public class GrisuMenu extends JMenuBar {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final GrisuMenu frame = new GrisuMenu(null);
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final Frame parent;

	private ServiceInterface si;

	private JMenu fileMenu;
	private JMenu toolsMenu;
	private JMenu helpMenu;

	private JMenuItem exitItem;
	private JMenuItem versionItem;

	private JLabel warningLabel;

	private final boolean isDevelopmentVersion;

	/**
	 * Create the frame.
	 */
	public GrisuMenu(Frame parent) {
		this.parent = parent;
		add(getFileMenu());
		add(getToolsMenu());

		// check whether development release - if so, use red background color
		String clientVersion = GrisuVersion.get("this-client");
		if (StringUtils.containsIgnoreCase(clientVersion, "snapshot")) {
			isDevelopmentVersion = true;
			setOpaque(true);
			setBackground(Color.RED);
			add(getDevelopmentWarningLabel());
		} else {
			isDevelopmentVersion = false;
		}
		setHelpMenu(getGrisuHelpMenu());

	}

	@Override
	public JMenu add(JMenu menu) {
		if (helpMenu != null) {
			return (JMenu) add(menu, getComponentCount() - 1);
		} else {
			return super.add(menu);
		}
	}

	private JLabel getDevelopmentWarningLabel() {
		if (warningLabel == null) {
			warningLabel = new JLabel(
					"         --- DEVELOPMENT VERSION ---         ");
			warningLabel.setBackground(Color.RED);
			warningLabel.setForeground(Color.WHITE);
			warningLabel.setOpaque(true);
		}
		return warningLabel;
	}

	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);
				}
			});
		}
		return exitItem;
	}

	public JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu("File");
			fileMenu.add(getExitItem());
		}
		return fileMenu;
	}

	private JMenu getGrisuHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu("Help");
			helpMenu.add(getVersionItem());
		}
		return helpMenu;
	}

	public JMenu getToolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu("Tools");
		}
		return toolsMenu;
	}

	private JMenuItem getVersionItem() {
		if (versionItem == null) {
			versionItem = new JMenuItem("About");
			versionItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					String clientVersion = GrisuVersion.get("this-client");

					String grisuclientversion = GrisuVersion
							.get("grisu-client");

					String message = "Version: " + clientVersion + "\n\n"
							+ "Grisu client lib version: " + grisuclientversion;

					JOptionPane.showMessageDialog(parent, message);

				}
			});
		}
		return versionItem;
	}

	public void remove(JMenu menu) {
		if (menu == helpMenu) {
			helpMenu = null;
		}
		super.remove(menu);
	}

	@Override
	public void removeAll() {
		super.removeAll();
		helpMenu = null;
	}

	@Override
	public void setHelpMenu(JMenu menu) {
		if (helpMenu != null) {
			remove(helpMenu);
		}
		helpMenu = menu;
		super.add(helpMenu);
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
	}
}
