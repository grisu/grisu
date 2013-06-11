package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.utils.ClientPropertiesHelper;
import grisu.frontend.view.swing.utils.MessageDialog;
import grisu.frontend.view.swing.utils.SettingsDialog;
import nz.org.nesi.envtester.TestController;
import nz.org.nesi.envtester.view.swing.EnvTestStartDialog;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

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

    private final GrisuApplicationWindow parent;

    private ServiceInterface si;

    private JMenu fileMenu;
    private JMenu toolsMenu;
    private JMenu helpMenu;

    private JMenuItem exitItem;
    private JMenuItem exitItemDeleteSession;
    private JMenuItem versionItem;
    private JMenuItem envtestItem;

    private JLabel warningLabel;

    private JMenuItem settingsItem;
    private final SettingsDialog dialog;

    private final boolean isDevelopmentVersion;

    /**
     * Create the frame.
     */
    public GrisuMenu(GrisuApplicationWindow parent) {
        this.parent = parent;

        dialog = new SettingsDialog(parent.getFrame());

        add(getFileMenu());
        add(getToolsMenu());

        getToolsMenu().add(getSettingsItem());

        // check whether development release - if so, use red background color
        final String clientVersion = LoginManager.getClientVersion();
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

    public void addSettingsPanel(String name, JPanel panel) {
        dialog.addPanel(name, panel);
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

    public JMenuItem getExitItem() {
        if (exitItem == null) {
            exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    parent.exit();
                    System.exit(0);
                }
            });
        }
        return exitItem;
    }

    public JMenuItem getExitDeleteSessionItem() {
        if (exitItemDeleteSession == null) {
            exitItemDeleteSession = new JMenuItem("Exit (and delete session)");
            exitItemDeleteSession.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {


                    parent.exit(true);
                    System.exit(0);
                }
            });
        }
        return exitItemDeleteSession;
    }

    public JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu("File");
            fileMenu.add(getExitItem());
            fileMenu.add(getExitDeleteSessionItem());
        }
        return fileMenu;
    }

    public JMenu getGrisuHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu("Help");
            helpMenu.add(getEnvtestItem());
            helpMenu.add(getVersionItem());
        }
        return helpMenu;
    }

    public JMenuItem getSettingsItem() {
        if (settingsItem == null) {
            settingsItem = new JMenuItem("Settings");
            settingsItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {

                    dialog.setVisible(true);

                }
            });
        }
        return settingsItem;
    }

    public JMenu getToolsMenu() {
        if (toolsMenu == null) {
            toolsMenu = new JMenu("Tools");
        }
        return toolsMenu;
    }

    public JMenuItem getVersionItem() {
        if (versionItem == null) {
            versionItem = new JMenuItem("About");
            versionItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    new Thread() {
                        public void run() {

                            Map<String, String> props = ClientPropertiesHelper.gatherClientProperties(si);

                            MessageDialog dialog = new MessageDialog();
                            dialog.setMessage(props);
                            dialog.pack();
                            dialog.setVisible(true);

                        }
                    }.start();

                }
            });
        }
        return versionItem;
    }

    public JMenuItem getEnvtestItem() {
        if (envtestItem == null) {
            envtestItem = new JMenuItem("Test network/environment");
            envtestItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    new Thread() {
                        public void run() {
                            TestController tc = new TestController("https://raw.github.com/nesi/nesi-grid-info/develop/env_test_config.groovy");
                            EnvTestStartDialog dialog = new EnvTestStartDialog(null, tc);
                            dialog.pack();
                            dialog.setVisible(true);
                        }
                    }.start();

                }
            });
        }
        return envtestItem;
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
