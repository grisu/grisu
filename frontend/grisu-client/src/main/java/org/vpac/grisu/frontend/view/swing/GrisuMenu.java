package org.vpac.grisu.frontend.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.frontend.view.swing.settings.SettingsDialog;

public class GrisuMenu extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrisuMenu frame = new GrisuMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final JPanel contentPane;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu toolsMenu;

	private JMenuItem settingsItem;
	private JMenuItem exitItem;

	/**
	 * Create the frame.
	 */
	public GrisuMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setJMenuBar(getMenuBar_1());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem("Exit");
		}
		return exitItem;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu("File");
			fileMenu.add(getExitItem());
		}
		return fileMenu;
	}

	private JMenuBar getMenuBar_1() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			menuBar.add(getFileMenu());
			menuBar.add(getToolsMenu());
		}
		return menuBar;
	}

	private JMenuItem getSettingsItem() {
		if (settingsItem == null) {
			settingsItem = new JMenuItem("Settings");
			settingsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					SettingsDialog dialog = new SettingsDialog();
					dialog.setServiceInterface(si);
					dialog.setVisible(true);

				}
			});
		}
		return settingsItem;
	}

	private JMenu getToolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu("Tools");
			toolsMenu.add(getSettingsItem());
		}
		return toolsMenu;
	}
}
