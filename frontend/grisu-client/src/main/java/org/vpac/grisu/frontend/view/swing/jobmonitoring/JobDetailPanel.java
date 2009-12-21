package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.files.preview.FileListWithPreviewPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jidesoft.swing.JideTabbedPane;

public class JobDetailPanel extends JPanel implements PropertyChangeListener {

	private JLabel lblJobname;
	private JTextField textField;
	private JLabel lblStatus;
	private JTextField textField_1;

	private JobObject job;
	private JideTabbedPane jideTabbedPane;
	private JScrollPane scrollPane;
	private JTextArea propertiesTextArea;
	private JScrollPane scrollPane_1;
	private JTextArea logTextArea;
	private FileListWithPreviewPanel fileListWithPreviewPanel;

	private final ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public JobDetailPanel(ServiceInterface si) {

		this.si = si;

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLblJobname(), "2, 2, right, default");
		add(getTextField(), "4, 2, fill, default");
		add(getLblStatus(), "2, 4, right, default");
		add(getTextField_1(), "4, 4, fill, default");
		add(getJideTabbedPane(), "2, 6, 3, 1, fill, fill");

	}

	private FileListWithPreviewPanel getFileListWithPreviewPanel() {
		if (fileListWithPreviewPanel == null) {
			fileListWithPreviewPanel = new FileListWithPreviewPanel(si, null,
					null, false, true, false, false);
		}
		return fileListWithPreviewPanel;
	}

	private JideTabbedPane getJideTabbedPane() {
		if (jideTabbedPane == null) {
			jideTabbedPane = new JideTabbedPane();
			jideTabbedPane.setTabPlacement(SwingConstants.TOP);
			jideTabbedPane.addTab("Job directory", null,
					getFileListWithPreviewPanel(), null);
			jideTabbedPane.addTab("Properties", getScrollPane_1());
			jideTabbedPane.addTab("Log", getScrollPane_1_1());
		}
		return jideTabbedPane;
	}

	private JLabel getLblJobname() {
		if (lblJobname == null) {
			lblJobname = new JLabel("Jobname");
		}
		return lblJobname;
	}

	private JLabel getLblStatus() {
		if (lblStatus == null) {
			lblStatus = new JLabel("Status");
		}
		return lblStatus;
	}

	private JTextArea getLogTextArea() {
		if (logTextArea == null) {
			logTextArea = new JTextArea();
		}
		return logTextArea;
	}

	private JTextArea getPropertiesTextArea() {
		if (propertiesTextArea == null) {
			propertiesTextArea = new JTextArea();
		}
		return propertiesTextArea;
	}

	private JScrollPane getScrollPane_1() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getPropertiesTextArea());
		}
		return scrollPane;
	}

	private JScrollPane getScrollPane_1_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getLogTextArea());
		}
		return scrollPane_1;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}

	private JTextField getTextField_1() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setEditable(false);
			textField_1.setColumns(10);
		}
		return textField_1;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals("status")) {
			getTextField_1()
					.setText(
							JobConstants.translateStatus((Integer) (evt
									.getNewValue())));
		}
	}

	public void setJob(JobObject job) {

		this.job = job;

		getFileListWithPreviewPanel().setRootUrl(job.getJobDirectoryUrl());
		getFileListWithPreviewPanel().setCurrentUrl(job.getJobDirectoryUrl());
		getTextField().setText(job.getJobname());
		getTextField_1().setText(
				JobConstants.translateStatus(job.getStatus(false)));

		setProperties();
		setLog();

	}

	private void setLog() {
		StringBuffer temp = new StringBuffer();

		for (Date date : job.getLogMessages().keySet()) {
			temp.append(date.toString() + ":\t"
					+ job.getLogMessages().get(date) + "\n");
		}

		getLogTextArea().setText(temp.toString());
	}

	private void setProperties() {

		StringBuffer temp = new StringBuffer();

		for (String key : job.getAllJobProperties().keySet()) {
			temp.append(key + "\t\t" + job.getAllJobProperties().get(key)
					+ "\n");
		}
		getPropertiesTextArea().setText(temp.toString());
	}
}
