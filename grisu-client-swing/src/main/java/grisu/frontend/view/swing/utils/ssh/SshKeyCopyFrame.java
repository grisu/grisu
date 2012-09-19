package grisu.frontend.view.swing.utils.ssh;

import grisu.frontend.control.login.LoginManager;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SshKeyCopyFrame extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {

		LoginManager.initGrisuClient("Ssh-key-copy-client");
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					SshKeyCopyFrame frame = new SshKeyCopyFrame(args);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	private JPanel contentPane;
	private PanSSHKeyCopyPanel panSSHKeyCopyPanel;

	private final String[] args;

	/**
	 * Create the frame.
	 */
	public SshKeyCopyFrame(String[] args) {
		this.args = args;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 674, 523);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getPanSSHKeyCopyPanel(), BorderLayout.CENTER);
	}

	private PanSSHKeyCopyPanel getPanSSHKeyCopyPanel() {
		if (panSSHKeyCopyPanel == null) {
			panSSHKeyCopyPanel = new PanSSHKeyCopyPanel();
			if (args.length >= 2) {
				panSSHKeyCopyPanel.setTemplatePath(args[0]);
				panSSHKeyCopyPanel.setMobaxtermpath(args[1]);
			}
			if (args.length == 3 ) {
				panSSHKeyCopyPanel.setBackend(args[2]);
			}
		}
		return panSSHKeyCopyPanel;
	}
}
