package grisu.frontend.view.swing.files;

import grisu.frontend.control.fileTransfers.FileTransaction;
import grisu.model.FileManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class FileTransferStatusPanel extends JPanel implements
		PropertyChangeListener {

	private JProgressBar progressBar;
	private JLabel statusLabel;
	private JTextField textField;

	private FileTransaction fileTransfer;

	/**
	 * Create the panel.
	 */
	public FileTransferStatusPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				RowSpec.decode("8dlu"), FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getProgressBar(), "2, 2, 3, 1, fill, default");
		add(getStatusLabel(), "2, 4, right, default");
		add(getTextField(), "4, 4, fill, default");

	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

	private JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel("Transaction:");
		}
		return statusLabel;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField("In progress...");
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		if ("status".equals(evt.getPropertyName())) {
			final FileTransaction.Status status = (FileTransaction.Status) evt
					.getNewValue();

			if (FileTransaction.Status.FAILED.equals(status)) {
				final String msg = "Failed: "
						+ fileTransfer.getException().getLocalizedMessage();
				getTextField().setText(msg);
			} else {
				getTextField().setText(status.toString());

			}

			if (FileTransaction.Status.FINISHED.equals(status)
					|| FileTransaction.Status.FAILED.equals(status)) {
				SwingUtilities.invokeLater(new Thread() {
					@Override
					public void run() {
						getProgressBar().setIndeterminate(false);
						getProgressBar()
								.setValue(getProgressBar().getMaximum());
					}
				});
			}
			// System.out.println("New status: "+((FileTransfer.Status)evt.getNewValue()).toString());
		} else if ("transferredSourceFiles".equals(evt.getPropertyName())) {
			if (fileTransfer.getTotalSourceFiles() > 1) {
				getProgressBar().setValue((Integer) evt.getNewValue());
			} else {
				getTextField().setText("In progress...");
			}
		} else if ("currentSourceFile".equals(evt.getPropertyName())) {
			getTextField().setText(
					FileManager.getFilename(evt.getNewValue().toString()));
			// System.out.println("Current file: "+evt.getNewValue());
		}

	}

	public void setFileTransaction(FileTransaction ft) {
		this.fileTransfer = ft;
		this.fileTransfer.addPropertyChangeListener(this);
		if (fileTransfer.getTotalSourceFiles() == 1) {
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setMinimum(0);
			progressBar.setMaximum(fileTransfer.getTotalSourceFiles());
			progressBar.setValue(fileTransfer.getTransferredSourceFiles());
		}
	}
}
