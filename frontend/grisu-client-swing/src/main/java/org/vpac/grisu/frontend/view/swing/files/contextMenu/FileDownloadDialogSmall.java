package org.vpac.grisu.frontend.view.swing.files.contextMenu;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.vpac.grisu.model.dto.GridFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.SwingConstants;

public class FileDownloadDialogSmall extends JDialog {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileDownloadDialogSmall dialog = new FileDownloadDialogSmall(
							null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JProgressBar progressBar;
	private JLabel lblDownloading;
	private JTextField fileTextField;

	private final GridFile file;

	/**
	 * Create the dialog.
	 */
	public FileDownloadDialogSmall(GridFile file) {
		setModal(false);
		this.file = file;
		setBounds(100, 100, 221, 121);
		getContentPane().setLayout(
				new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, }));
		getContentPane().add(getProgressBar(), "2, 2");
		getContentPane().add(getLblDownloading(), "2, 4");
		getContentPane().add(getFileTextField(), "2, 6, fill, default");
		getFileTextField().setText(file.getName());
		setVisible(true);
	}

	public void close() {
		this.dispose();
	}

	private JTextField getFileTextField() {
		if (fileTextField == null) {
			fileTextField = new JTextField();
			fileTextField.setHorizontalAlignment(SwingConstants.CENTER);
			fileTextField.setEditable(false);
			fileTextField.setColumns(10);
		}
		return fileTextField;
	}

	private JLabel getLblDownloading() {
		if (lblDownloading == null) {
			lblDownloading = new JLabel("Downloading:");
		}
		return lblDownloading;
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
		}
		return progressBar;
	}
}
