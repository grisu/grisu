package org.vpac.grisu.frontend.view.swing.files;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vpac.grisu.frontend.control.fileTransfers.FileTransaction;
import org.vpac.grisu.model.FileManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FIleTransferStatusPanel extends JPanel implements PropertyChangeListener {

	private JProgressBar progressBar;
	private JLabel statusLabel;
	private JTextField textField;

	private final FileTransaction fileTransfer;

	/**
	 * Create the panel.
	 */
	public FIleTransferStatusPanel(FileTransaction ft) {
		this.fileTransfer = ft;
		this.fileTransfer.addPropertyChangeListener(this);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				RowSpec.decode("8dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getProgressBar(), "2, 2, 3, 1, fill, default");
		add(getStatusLabel(), "2, 4, right, default");
		add(getTextField(), "4, 4, fill, default");

	}
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			if ( fileTransfer.getTotalSourceFiles() == 1 ) {
				progressBar.setIndeterminate(true);
			} else {
				progressBar.setMinimum(0);
				progressBar.setMaximum(fileTransfer.getTotalSourceFiles());
				progressBar.setValue(fileTransfer.getTransferredSourceFiles());
			}
		}
		return progressBar;
	}
	private JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel("Transfer:");
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

		if ( "status".equals(evt.getPropertyName()) ) {
			FileTransaction.Status status = (FileTransaction.Status)evt.getNewValue();
			getTextField().setText(status.toString());
			if ( FileTransaction.Status.FINISHED.equals(status) || FileTransaction.Status.FAILED.equals(status) ) {
				getProgressBar().setIndeterminate(false);
				getProgressBar().setValue(getProgressBar().getMaximum());
			}
			//			System.out.println("New status: "+((FileTransfer.Status)evt.getNewValue()).toString());
		} else if ( "transferredSourceFiles".equals(evt.getPropertyName()) ) {
			if ( fileTransfer.getTotalSourceFiles() > 1 ) {
				getProgressBar().setValue((Integer)evt.getNewValue());
			} else {
				getTextField().setText("In progress...");
			}
		} else if ( "currentSourceFile".equals(evt.getPropertyName()) ) {
			getTextField().setText(FileManager.getFilename(evt.getNewValue().toString()));
			//System.out.println("Current file: "+evt.getNewValue());
		}

	}
}
