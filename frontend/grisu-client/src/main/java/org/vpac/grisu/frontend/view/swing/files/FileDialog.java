package org.vpac.grisu.frontend.view.swing.files;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;

public class FileDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			
			String username = args[0];
			char[] password = args[1].toCharArray();

			LoginParams loginParams = new LoginParams(
			// "http://localhost:8080/grisu-ws/services/grisu",
//					 "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
//					"http://localhost:8080/enunciate-backend/soap/GrisuService",
			"Local", 
//			"LOCAL_WS",
			username, password);
			
			
			ServiceInterface si = null;
//			si = LoginManager.login(null, password, username, "VPAC", loginParams);
			si = LoginManager.login(null, null, null, null, loginParams);
			
			FileDialog dialog = new FileDialog(si);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FileDialog(ServiceInterface si) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			FileListPanel fileListPanel = new FileListPanel(si, (String) null);
			contentPanel.add(fileListPanel, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
