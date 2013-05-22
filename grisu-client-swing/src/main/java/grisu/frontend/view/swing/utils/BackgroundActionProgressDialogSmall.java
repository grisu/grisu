package grisu.frontend.view.swing.utils;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class BackgroundActionProgressDialogSmall extends JDialog {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final BackgroundActionProgressDialogSmall dialog = new BackgroundActionProgressDialogSmall(
							null, null);
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JProgressBar progressBar;
	private JLabel lblDownloading;
	private JTextField fileTextField;

	private final String label;
	private final String item;

	/**
	 * Create the dialog.
	 */
	public BackgroundActionProgressDialogSmall(String label, String item) {
		setModal(false);
		this.label = label;
		this.item = item;
		setBounds(100, 100, 221, 121);
		getContentPane().setLayout(
				new FormLayout(new ColumnSpec[] {
						FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, }));
		getContentPane().add(getProgressBar(), "2, 2");
		getContentPane().add(getLblDownloading(), "2, 4");
		getContentPane().add(getFileTextField(), "2, 6, fill, default");
		getFileTextField().setText(item);
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
			lblDownloading = new JLabel(label);
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
