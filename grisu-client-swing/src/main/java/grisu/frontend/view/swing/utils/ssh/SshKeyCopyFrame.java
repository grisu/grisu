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
	public static void main(String[] args) {

		LoginManager.initEnvironment();
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					SshKeyCopyFrame frame = new SshKeyCopyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel contentPane;
	private PanSSHKeyCopyPanel panSSHKeyCopyPanel;

	/**
	 * Create the frame.
	 */
	public SshKeyCopyFrame() {
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
		}
		return panSSHKeyCopyPanel;
	}
}
