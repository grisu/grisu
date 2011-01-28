package grisu.frontend.view.swing.utils;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;


public class SettingsDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final SettingsDialog dialog = new SettingsDialog(null);

			final ServiceInterface si = LoginManager.loginCommandline();
			dialog.setServiceInterface(si);

			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private ServiceInterface si;

	private final JPanel contentPanel = new JPanel();
	final JTabbedPane tabbedPane;

	/**
	 * Create the dialog.
	 */
	public SettingsDialog(Frame parent) {
		super(parent);
		setTitle("Settings");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 701, 522);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(SwingConstants.TOP);
			contentPanel.add(tabbedPane, BorderLayout.CENTER);

		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public void addPanel(String name, JPanel panel) {
		tabbedPane.insertTab(name, null, panel, null, 0);
		tabbedPane.setSelectedIndex(0);
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		// if (applicationSubscribePanel != null) {
		// applicationSubscribePanel.setServiceInterface(si);
		// }
	}

}
