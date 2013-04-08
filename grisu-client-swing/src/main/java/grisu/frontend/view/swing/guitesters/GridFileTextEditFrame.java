package grisu.frontend.view.swing.guitesters;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.swing.files.GridFileTextEditPanel;
import grisu.frontend.view.swing.files.open.FileDialogManager;
import grisu.frontend.view.swing.files.open.FileDialogManager.OpenFileAction;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GridFileTextEditFrame extends JFrame {

	private JPanel contentPane;
	private GridFileTextEditPanel gridFileTextEditPanel;
	private JButton btnOpen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		
		final ServiceInterface si = LoginManager.login("nesi");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GridFileTextEditFrame frame = new GridFileTextEditFrame();
					frame.setServiceInterface(si);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GridFileTextEditFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(getGridFileTextEditPanel(), BorderLayout.CENTER);
		contentPane.add(getBtnOpen(), BorderLayout.SOUTH);
	}

	private GridFileTextEditPanel getGridFileTextEditPanel() {
		if (gridFileTextEditPanel == null) {
			gridFileTextEditPanel = new GridFileTextEditPanel("Default");
		}
		return gridFileTextEditPanel;
	}
	private JButton getBtnOpen() {
		if (btnOpen == null) {
			btnOpen = new JButton("open");

		}
		return btnOpen;
	}
	
	public void setServiceInterface(ServiceInterface si) {
		getGridFileTextEditPanel().setServiceInterface(si);
		OpenFileAction oa = FileDialogManager.getDefault(si, this).createOpenAction(getGridFileTextEditPanel(), "default");
		getBtnOpen().setAction(oa);
	}
}
