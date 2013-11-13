package grisu.frontend.view.swing.utils;

import grisu.control.ServiceInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// try {
		// final SettingsDialog dialog = new SettingsDialog(null);
		//
		// final ServiceInterface si = LoginManager.loginCommandline();
		// dialog.setServiceInterface(si);
		//
		// dialog.setVisible(true);
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
	}

	private ServiceInterface si;

	private final JPanel contentPanel = new JPanel();
	final JTabbedPane tabbedPane;

	/**
	 * Create the dialog.
	 */
	public SettingsDialog(Frame parent) {
		super(parent);
        setModal(false);
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
        setLocationRelativeTo(parent);

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
