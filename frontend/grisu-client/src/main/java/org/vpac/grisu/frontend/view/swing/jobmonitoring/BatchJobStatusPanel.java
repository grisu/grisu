package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobStatusPanel extends JPanel implements
		PropertyChangeListener {
	private JLabel lblStatus;
	private JLabel lblUnsubmitted;
	private JProgressBar unsubProgress;
	private JLabel lblWaiting;
	private JProgressBar waitingProgress;
	private JLabel lblRun;
	private JProgressBar runProgress;
	private JLabel lblFail;
	private JProgressBar failProgress;
	private JLabel lblDone;
	private JProgressBar doneProgress;

	private final BatchJobObject bj;
	private JLabel totalLabel;
	private JLabel label;

	/**
	 * Create the panel.
	 */
	public BatchJobStatusPanel(BatchJobObject bj) {
		this.bj = bj;
		this.bj.addPropertyChangeListener(this);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(20dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				ColumnSpec.decode("12dlu"), ColumnSpec.decode("24dlu"), },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, RowSpec.decode("12dlu"),
						RowSpec.decode("top:default:grow"),
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("bottom:default:grow"),
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));
		add(getLblStatus(), "4, 2");
		add(getTotalLabel(), "2, 4, right, default");
		add(getUnsubProgress(), "4, 4, 1, 3, center, default");
		add(getWaitingProgress(), "6, 4, 1, 3, center, default");
		add(getRunProgress(), "8, 4, 1, 3, center, default");
		add(getFailProgress(), "10, 4, 1, 3, center, default");
		add(getDoneProgress(), "12, 4, 1, 3, center, default");
		add(getLabel(), "2, 6, right, default");
		add(getLblUnsubmitted(), "4, 8, center, default");
		add(getLblWaiting(), "6, 8, center, default");
		add(getLblRun(), "8, 8, center, default");
		add(getLblFail(), "10, 8, center, default");
		add(getLblDone(), "12, 8, center, default");

		refreshProgressBars();

	}

	private JProgressBar getDoneProgress() {
		if (doneProgress == null) {
			doneProgress = new JProgressBar();
			doneProgress.setMinimum(0);
			doneProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return doneProgress;
	}

	private JProgressBar getFailProgress() {
		if (failProgress == null) {
			failProgress = new JProgressBar();
			failProgress.setMinimum(0);
			failProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return failProgress;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("0");
		}
		return label;
	}

	private JLabel getLblDone() {
		if (lblDone == null) {
			lblDone = new JLabel("Done");
		}
		return lblDone;
	}

	private JLabel getLblFail() {
		if (lblFail == null) {
			lblFail = new JLabel("Fail");
		}
		return lblFail;
	}

	private JLabel getLblRun() {
		if (lblRun == null) {
			lblRun = new JLabel("Run");
		}
		return lblRun;
	}

	private JLabel getLblStatus() {
		if (lblStatus == null) {
			lblStatus = new JLabel("Status");
		}
		return lblStatus;
	}

	private JLabel getLblUnsubmitted() {
		if (lblUnsubmitted == null) {
			lblUnsubmitted = new JLabel("Unsub");
		}
		return lblUnsubmitted;
	}

	private JLabel getLblWaiting() {
		if (lblWaiting == null) {
			lblWaiting = new JLabel("Waiting");
		}
		return lblWaiting;
	}

	private JProgressBar getRunProgress() {
		if (runProgress == null) {
			runProgress = new JProgressBar();
			runProgress.setMinimum(0);
			runProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return runProgress;
	}

	private JLabel getTotalLabel() {
		if (totalLabel == null) {
			totalLabel = new JLabel("n/a");
			totalLabel.setVerticalAlignment(SwingConstants.TOP);
		}
		return totalLabel;
	}

	private JProgressBar getUnsubProgress() {
		if (unsubProgress == null) {
			unsubProgress = new JProgressBar();
			unsubProgress.setMinimum(0);
			unsubProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return unsubProgress;
	}

	private JProgressBar getWaitingProgress() {
		if (waitingProgress == null) {
			waitingProgress = new JProgressBar();
			waitingProgress.setMinimum(0);
			waitingProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return waitingProgress;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		if (BatchJobObject.STATUS.equals(evt.getPropertyName())) {
			// TODO
		} else if (BatchJobObject.TOTAL_NUMBER_OF_JOBS.equals(evt
				.getPropertyName())) {

			refreshProgressBars();

		} else if (BatchJobObject.NUMBER_OF_UNSUBMITTED_JOBS.equals(evt
				.getPropertyName())) {

			getUnsubProgress().setValue((Integer) evt.getNewValue());

		} else if (BatchJobObject.NUMBER_OF_WAITING_JOBS.equals(evt
				.getPropertyName())) {
			getWaitingProgress().setValue((Integer) evt.getNewValue());
		} else if (BatchJobObject.NUMBER_OF_RUNNING_JOBS.equals(evt
				.getPropertyName())) {
			getRunProgress().setValue((Integer) evt.getNewValue());
		} else if (BatchJobObject.NUMBER_OF_FAILED_JOBS.equals(evt
				.getPropertyName())) {
			getFailProgress().setValue((Integer) evt.getNewValue());
		} else if (BatchJobObject.NUMBER_OF_SUCCESSFULL_JOBS.equals(evt
				.getPropertyName())) {
			getDoneProgress().setValue((Integer) evt.getNewValue());
		} else if (BatchJobObject.NUMBER_OF_FINISHED_JOBS.equals(evt
				.getPropertyName())) {
			// TODO
		}

	}

	private void refreshProgressBars() {

		getDoneProgress().setMaximum(bj.getTotalNumberOfJobs());
		getDoneProgress().setValue(bj.getNumberOfSuccessfulJobs());

		getFailProgress().setMaximum(bj.getTotalNumberOfJobs());
		getFailProgress().setValue(bj.getNumberOfFailedJobs());

		getRunProgress().setMaximum(bj.getTotalNumberOfJobs());
		getRunProgress().setValue(bj.getNumberOfRunningJobs());

		getUnsubProgress().setMaximum(bj.getTotalNumberOfJobs());
		getUnsubProgress().setValue(bj.getNumberOfUnsubmittedJobs());

		getWaitingProgress().setMaximum(bj.getTotalNumberOfJobs());
		getWaitingProgress().setValue(bj.getNumberOfWaitingJobs());

	}
}
