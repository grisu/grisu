package org.vpac.grisu.frontend.view.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobSubmissionLogPanel extends JPanel implements
		PropertyChangeListener {
	private JLabel lblMonitoringJob;
	private JTextField textField;
	private JScrollPane scrollPane;
	private JTextArea logArea;

	private BatchJobObject batchJob;

	private JobObject singleJob;

	/**
	 * Create the panel.
	 */
	public JobSubmissionLogPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLblMonitoringJob(), "2, 2, right, default");
		add(getTextField(), "4, 2, fill, default");
		add(getScrollPane(), "2, 4, 3, 1, fill, fill");

	}

	public void clear() {

		getLogArea().setText("");
		getTextField().setText("");

	}

	public void fillTextBox() {
		if (singleJob == null && batchJob == null) {
			logArea.setText("No job associated yet.");
			return;
		}

		final StringBuffer temp = new StringBuffer();
		if (singleJob != null) {
			for (String line : singleJob.getSubmissionLog()) {
				temp.append(line + "\n");
			}

		} else if (batchJob != null) {

			for (String line : batchJob.getSubmissionLog()) {
				temp.append(line + "\n");
			}

		}

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				logArea.setText(temp.toString());
			}

		});
	}

	public BatchJobObject getBatchJob() {
		return batchJob;
	}

	private JLabel getLblMonitoringJob() {
		if (lblMonitoringJob == null) {
			lblMonitoringJob = new JLabel("Monitoring Job:");
		}
		return lblMonitoringJob;
	}

	private JTextArea getLogArea() {
		if (logArea == null) {
			logArea = new JTextArea();
		}
		return logArea;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getLogArea());
		}
		return scrollPane;
	}

	public JobObject getSingleJob() {
		return singleJob;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setEditable(false);
			textField.setColumns(10);
		}
		return textField;
	}

	public void propertyChange(PropertyChangeEvent arg0) {

		if ("submissionLog".equals(arg0.getPropertyName())) {
			fillTextBox();
		} else if ("submitting".equals(arg0.getPropertyName())) {
			if ((Boolean) arg0.getOldValue() && !(Boolean) arg0.getNewValue()) {
				getLogArea().append("Job submission finished.");
			}
		}
	}

	public void setBatchJob(BatchJobObject batchJob) {
		if (this.singleJob != null) {
			singleJob.removePropertyChangeListener(this);
			this.singleJob = null;
		}
		if (this.batchJob != null) {
			batchJob.removePropertyChangeListener(this);
		}
		this.batchJob = batchJob;
		this.batchJob.addPropertyChangeListener(this);

		getTextField().setText(this.batchJob.getJobname());

	}

	public void setSingleJob(JobObject singleJob) {
		if (this.batchJob != null) {
			this.batchJob.removePropertyChangeListener(this);
			this.batchJob = null;
		}
		if (this.singleJob != null) {
			this.singleJob.removePropertyChangeListener(this);
		}
		this.singleJob = singleJob;
		this.singleJob.addPropertyChangeListener(this);

		getLblMonitoringJob().setText(this.singleJob.getJobname());

		fillTextBox();
	}
}
