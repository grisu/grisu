package org.vpac.grisu.frontend.view.swing.jobmonitoring;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobControlPanel extends JPanel {
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

	/**
	 * Create the panel.
	 */
	public BatchJobControlPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("24dlu"),
				ColumnSpec.decode("12dlu"), ColumnSpec.decode("24dlu"), },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, RowSpec.decode("12dlu"),
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));
		add(getLblStatus(), "2, 2");
		add(getUnsubProgress(), "2, 4, center, default");
		add(getProgressBar_2_1(), "4, 4, center, default");
		add(getRunProgress(), "6, 4, center, default");
		add(getFailProgress(), "8, 4, center, default");
		add(getDoneProgress(), "10, 4, center, default");
		add(getLblUnsubmitted(), "2, 6, center, default");
		add(getLblWaiting(), "4, 6, center, default");
		add(getLblRun(), "6, 6, center, default");
		add(getLblFail(), "8, 6, center, default");
		add(getLblDone(), "10, 6, center, default");

	}

	private JProgressBar getDoneProgress() {
		if (doneProgress == null) {
			doneProgress = new JProgressBar();
			doneProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return doneProgress;
	}

	private JProgressBar getFailProgress() {
		if (failProgress == null) {
			failProgress = new JProgressBar();
			failProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return failProgress;
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

	private JProgressBar getProgressBar_2_1() {
		if (waitingProgress == null) {
			waitingProgress = new JProgressBar();
			waitingProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return waitingProgress;
	}

	private JProgressBar getRunProgress() {
		if (runProgress == null) {
			runProgress = new JProgressBar();
			runProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return runProgress;
	}

	private JProgressBar getUnsubProgress() {
		if (unsubProgress == null) {
			unsubProgress = new JProgressBar();
			unsubProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return unsubProgress;
	}
}
