package grisu.frontend.view.swing.utils;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableMap;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import grisu.control.ServiceInterface;
import grisu.frontend.view.swing.ServiceInterfacePanel;
import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoProperties;
import grisu.model.info.dto.DtoStringList;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: markus
 * Date: 23/05/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminPanel implements ServiceInterfacePanel {

    private ServiceInterface si;

    private JPanel panel1;

    private JButton reloadConfigButton = new JButton();
    private JButton reloadInfoButton = new JButton();
    private JButton reloadTemplatesButton = new JButton();
    private JButton clearUserCacheButton = new JButton();
    private JTextField userField = new JTextField();
    private JButton listUserButton = new JButton();
    private JTextField listUserField = new JTextField();

    public AdminPanel() {

        $$$setupUI$$$();


        reloadConfigButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        DtoStringList result = getServiceInterface().admin(Constants.REFRESH_CONFIG, null);
                        showResult(result);
                    }
                }.start();

            }
        });
        reloadInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        DtoStringList result = getServiceInterface().admin(Constants.REFRESH_GRID_INFO, null);
                        showResult(result);
                    }
                }.start();
            }
        });
        reloadTemplatesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        DtoStringList result = getServiceInterface().admin(Constants.REFRESH_TEMPLATES, null);
                        showResult(result);
                    }
                }.start();
            }
        });
        clearUserCacheButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        Map<String, String> config = ImmutableMap.of(Constants.USER, getUser());
                        DtoStringList result = getServiceInterface().admin(Constants.CLEAR_USER_CACHE, DtoProperties.createProperties(config));
                        List<String> msg = Lists.newArrayList();
                        msg.add("Cached cleard for:");
                        msg.add("");
                        if (result != null) {
                            msg.addAll(result.getStringList());
                        } else {
                            msg.add("n/a");
                        }
                        showResult(msg);
                    }
                }.start();
            }
        });
        listUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        Map<String, String> config = ImmutableMap.of(Constants.USER, getListUser());
                        DtoStringList result = getServiceInterface().admin(Constants.LIST_USERS, DtoProperties.createProperties(config));
                        showResult(result);
                    }
                }.start();
            }
        });
    }

    public JPanel getPanel() {
        return panel1;
    }

    public String getPanelTitle() {
        return "Admin";
    }

    public void setServiceInterface(ServiceInterface si) {
        this.si = si;
    }

    public ServiceInterface getServiceInterface() {
        return this.si;
    }

    public String getUser() {
        String user = userField.getText();
        if (StringUtils.isBlank(user)) {
            user = Constants.ALL_USERS;
        }
        return user;
    }

    public String getListUser() {
        String user = listUserField.getText();
        if (StringUtils.isBlank(user)) {
            user = Constants.ALL_USERS;
        }
        return user;
    }

    private void showResult(List<String> msg) {

        if (msg == null) {
            msg = Lists.newArrayList();
            msg.add("n/a");
        }
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setText(StringUtils.join(msg, "\n").toString());
        text.setLineWrap(false);
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new Dimension(320, 240));

        JDialog dialog = new JDialog();
        dialog.setTitle("Admin command");
        dialog.add(scrollPane);

        dialog.pack();
        dialog.setSize(new Dimension(400, 350));

        dialog.setVisible(true);
    }

    private void showResult(DtoStringList msg) {
        showResult(msg.getStringList());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:36px:noGrow,left:4dlu:noGrow,fill:d:noGrow,left:28dlu:noGrow,right:109px:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:42dlu:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,fill:max(d;4px):noGrow", "center:25px:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        final JLabel label1 = new JLabel();
        label1.setText("Reload config");
        CellConstraints cc = new CellConstraints();
        panel1.add(label1, cc.xy(3, 3));
        final JLabel label2 = new JLabel();
        label2.setText("Reload info");
        panel1.add(label2, cc.xy(3, 5));
        final JLabel label3 = new JLabel();
        label3.setText("Reload templates");
        panel1.add(label3, cc.xy(3, 7));
        final JLabel label4 = new JLabel();
        label4.setText("Clear user cache");
        panel1.add(label4, cc.xy(3, 9));
        reloadInfoButton.setIcon(new ImageIcon(getClass().getResource("/refresh.png")));
        reloadInfoButton.setText("");
        panel1.add(reloadInfoButton, cc.xy(8, 5));
        reloadTemplatesButton = new JButton();
        reloadTemplatesButton.setIcon(new ImageIcon(getClass().getResource("/refresh.png")));
        reloadTemplatesButton.setText("");
        panel1.add(reloadTemplatesButton, cc.xy(8, 7));
        clearUserCacheButton = new JButton();
        clearUserCacheButton.setIcon(new ImageIcon(getClass().getResource("/refresh.png")));
        clearUserCacheButton.setText("");
        panel1.add(clearUserCacheButton, cc.xy(8, 9));
        reloadConfigButton = new JButton();
        reloadConfigButton.setIcon(new ImageIcon(getClass().getResource("/refresh.png")));
        reloadConfigButton.setText("");
        panel1.add(reloadConfigButton, cc.xy(8, 3));
        userField = new JTextField();
        panel1.add(userField, cc.xy(5, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label5 = new JLabel();
        label5.setText("Get a list of all users");
        panel1.add(label5, cc.xy(3, 11));
        listUserButton = new JButton();
        listUserButton.setIcon(new ImageIcon(getClass().getResource("/help_icon.gif")));
        listUserButton.setText("");
        panel1.add(listUserButton, cc.xy(8, 11));
        listUserField = new JTextField();
        panel1.add(listUserField, cc.xy(5, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
