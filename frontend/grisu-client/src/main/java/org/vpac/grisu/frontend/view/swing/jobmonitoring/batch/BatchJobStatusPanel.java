package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.vpac.grisu.frontend.model.job.BatchJobObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import externalHelpers.ComponentTitledBorder;
import furbelow.SpinningDial;

public class BatchJobStatusPanel extends JPanel implements
PropertyChangeListener {
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
	private JLabel unsubValueLabel;
	private JLabel waitingValueLabel;
	private JLabel runValueLabel;
	private JLabel failValueLabel;
	private JLabel doneValueLabel;
	private JSeparator separator;
	private JSeparator separator_1;
	private JSeparator separator_2;
	private JSeparator separator_3;
	private JSeparator separator_4;
	private JSeparator separator_5;
	private JSeparator separator_6;
	private JSeparator separator_7;
	private JSeparator separator_8;
	private JSeparator separator_9;
	private JSeparator separator_10;
	private JSeparator separator_11;
	private JSeparator separator_12;
	private JSeparator separator_13;
	private JSeparator separator_14;

	private ComponentTitledBorder border = null;
	private final JLabel borderLabel;

	private static SpinningDial LOADING_ICON = new SpinningDial(16, 16);

	/**
	 * Create the panel.
	 */
	public BatchJobStatusPanel(BatchJobObject bj) {

		borderLabel = new JLabel("Status");
		borderLabel.setOpaque(true);


		borderLabel.setHorizontalTextPosition(SwingConstants.LEFT);

		border = new ComponentTitledBorder(borderLabel, this
				, BorderFactory.createEtchedBorder());

		//		setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setBorder(border);

		this.bj = bj;
		this.bj.addPropertyChangeListener(this);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(20dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("12dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("12dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("12dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("12dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("12dlu"),
				ColumnSpec.decode("14dlu"),
				ColumnSpec.decode("12dlu"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				RowSpec.decode("8dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:max(24dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("bottom:max(24dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getUnsubValueLabel(), "4, 2, 2, 1, right, default");
		add(getWaitingValueLabel(), "7, 2, 2, 1, right, default");
		add(getRunValueLabel(), "10, 2, 2, 1, right, default");
		add(getFailValueLabel(), "13, 2, 2, 1, right, default");
		add(getTotalLabel(), "2, 4, 1, 3, right, default");
		add(getSeparator(), "4, 4");
		add(getUnsubProgress(), "5, 4, 1, 5, right, default");
		add(getSeparator_1(), "7, 4");
		add(getWaitingProgress(), "8, 4, 1, 5, right, default");
		add(getSeparator_2(), "10, 4");
		add(getRunProgress(), "11, 4, 1, 5, right, default");
		add(getSeparator_3(), "13, 4");
		add(getFailProgress(), "14, 4, 1, 5, right, default");
		add(getSeparator_4(), "16, 4, 2, 1");
		add(getDoneProgress(), "18, 4, 1, 5, right, default");
		add(getDoneValueLabel(), "17, 2, 2, 1, right, default");
		add(getSeparator_11(), "3, 6, 2, 1");
		add(getSeparator_12(), "7, 6");
		add(getSeparator_13(), "10, 6");
		add(getSeparator_14(), "13, 6");
		add(getSeparator_10(), "16, 6, 2, 1");
		add(getLabel(), "2, 8, right, default");
		add(getSeparator_5(), "4, 8");
		add(getSeparator_6(), "7, 8");
		add(getSeparator_7(), "10, 8");
		add(getSeparator_8(), "13, 8");
		add(getSeparator_9(), "16, 8, 2, 1");
		add(getLblUnsubmitted(), "4, 10, 2, 1, right, default");
		add(getLblWaiting(), "7, 10, 2, 1, right, default");
		add(getLblRun(), "10, 10, 2, 1, right, default");
		add(getLblFail(), "13, 10, 2, 1, right, default");
		add(getLblDone(), "18, 10, 2, 1, right, default");



		refreshProgressBars();

		if ( bj.isRefreshing() ) {
			borderLabel.setIcon(LOADING_ICON);
		} else {
			borderLabel.setIcon(null);
		}
		repaint();


	}

	private JProgressBar getDoneProgress() {
		if (doneProgress == null) {
			doneProgress = new JProgressBar();
			doneProgress.setMinimum(0);
			doneProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return doneProgress;
	}

	private JLabel getDoneValueLabel() {
		if (doneValueLabel == null) {
			doneValueLabel = new JLabel("");
			doneValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			doneValueLabel.setFont(doneValueLabel.getFont().deriveFont(doneValueLabel.getFont().getStyle() | Font.BOLD));
		}
		return doneValueLabel;
	}

	private JProgressBar getFailProgress() {
		if (failProgress == null) {
			failProgress = new JProgressBar();
			failProgress.setMinimum(0);
			failProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return failProgress;
	}

	private JLabel getFailValueLabel() {
		if (failValueLabel == null) {
			failValueLabel = new JLabel("");
			failValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return failValueLabel;
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
			lblFail = new JLabel("Failed");
		}
		return lblFail;
	}

	private JLabel getLblRun() {
		if (lblRun == null) {
			lblRun = new JLabel("Running");
		}
		return lblRun;
	}

	private JLabel getLblUnsubmitted() {
		if (lblUnsubmitted == null) {
			lblUnsubmitted = new JLabel("Unsubmitted");
		}
		return lblUnsubmitted;
	}

	private JLabel getLblWaiting() {
		if (lblWaiting == null) {
			lblWaiting = new JLabel("Pending");
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

	private JLabel getRunValueLabel() {
		if (runValueLabel == null) {
			runValueLabel = new JLabel("");
			runValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return runValueLabel;
	}

	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
		}
		return separator;
	}

	private JSeparator getSeparator_1() {
		if (separator_1 == null) {
			separator_1 = new JSeparator();
		}
		return separator_1;
	}
	private JSeparator getSeparator_10() {
		if (separator_10 == null) {
			separator_10 = new JSeparator();
		}
		return separator_10;
	}
	private JSeparator getSeparator_11() {
		if (separator_11 == null) {
			separator_11 = new JSeparator();
		}
		return separator_11;
	}
	private JSeparator getSeparator_12() {
		if (separator_12 == null) {
			separator_12 = new JSeparator();
		}
		return separator_12;
	}
	private JSeparator getSeparator_13() {
		if (separator_13 == null) {
			separator_13 = new JSeparator();
		}
		return separator_13;
	}
	private JSeparator getSeparator_14() {
		if (separator_14 == null) {
			separator_14 = new JSeparator();
		}
		return separator_14;
	}
	private JSeparator getSeparator_2() {
		if (separator_2 == null) {
			separator_2 = new JSeparator();
		}
		return separator_2;
	}
	private JSeparator getSeparator_3() {
		if (separator_3 == null) {
			separator_3 = new JSeparator();
		}
		return separator_3;
	}
	private JSeparator getSeparator_4() {
		if (separator_4 == null) {
			separator_4 = new JSeparator();
		}
		return separator_4;
	}
	private JSeparator getSeparator_5() {
		if (separator_5 == null) {
			separator_5 = new JSeparator();
		}
		return separator_5;
	}
	private JSeparator getSeparator_6() {
		if (separator_6 == null) {
			separator_6 = new JSeparator();
		}
		return separator_6;
	}
	private JSeparator getSeparator_7() {
		if (separator_7 == null) {
			separator_7 = new JSeparator();
		}
		return separator_7;
	}
	private JSeparator getSeparator_8() {
		if (separator_8 == null) {
			separator_8 = new JSeparator();
		}
		return separator_8;
	}
	private JSeparator getSeparator_9() {
		if (separator_9 == null) {
			separator_9 = new JSeparator();
		}
		return separator_9;
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
	private JLabel getUnsubValueLabel() {
		if (unsubValueLabel == null) {
			unsubValueLabel = new JLabel("");
			unsubValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return unsubValueLabel;
	}
	private JProgressBar getWaitingProgress() {
		if (waitingProgress == null) {
			waitingProgress = new JProgressBar();
			waitingProgress.setMinimum(0);
			waitingProgress.setOrientation(SwingConstants.VERTICAL);
		}
		return waitingProgress;
	}
	private JLabel getWaitingValueLabel() {
		if (waitingValueLabel == null) {
			waitingValueLabel = new JLabel("");
			waitingValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return waitingValueLabel;
	}
	public void propertyChange(final PropertyChangeEvent evt) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public  void run() {

				if (BatchJobObject.STATUS.equals(evt.getPropertyName())) {
					// TODO
				} else if (BatchJobObject.TOTAL_NUMBER_OF_JOBS.equals(evt
						.getPropertyName())) {

					refreshProgressBars();

				} else if (BatchJobObject.NUMBER_OF_UNSUBMITTED_JOBS.equals(evt
						.getPropertyName())) {

					int i = (Integer) evt.getNewValue();
					getUnsubProgress().setValue(i);
					if ( i > 0 ) {
						getUnsubValueLabel().setText(""+i);
					} else {
						getUnsubValueLabel().setText("");
					}

				} else if (BatchJobObject.NUMBER_OF_WAITING_JOBS.equals(evt
						.getPropertyName())) {
					int i = (Integer) evt.getNewValue();
					getWaitingProgress().setValue(i);
					if ( i > 0 ) {
						getWaitingValueLabel().setText(""+i);
					} else {
						getWaitingValueLabel().setText("");
					}
				} else if (BatchJobObject.NUMBER_OF_RUNNING_JOBS.equals(evt
						.getPropertyName())) {
					int i = (Integer) evt.getNewValue();
					getRunProgress().setValue(i);
					if ( i > 0 ) {
						getRunValueLabel().setText(""+i);
					} else {
						getRunValueLabel().setText("");
					}
				} else if (BatchJobObject.NUMBER_OF_FAILED_JOBS.equals(evt
						.getPropertyName())) {
					int i = (Integer) evt.getNewValue();
					getFailProgress().setValue(i);
					if ( i > 0 ) {
						getFailValueLabel().setText(""+i);
					} else {
						getFailValueLabel().setText("");
					}
				} else if (BatchJobObject.NUMBER_OF_SUCCESSFULL_JOBS.equals(evt
						.getPropertyName())) {
					int i = (Integer) evt.getNewValue();
					getDoneProgress().setValue(i);
					if ( i > 0 ) {
						getDoneValueLabel().setText(""+i);
					} else {
						getDoneValueLabel().setText("");
					}
				} else if (BatchJobObject.NUMBER_OF_FINISHED_JOBS.equals(evt
						.getPropertyName())) {
					//TODO
				} else if (BatchJobObject.REFRESHING.equals(evt.getPropertyName())) {
					if ( (Boolean)evt.getNewValue() ) {
						borderLabel.setIcon(LOADING_ICON);
					} else {
						borderLabel.setIcon(null);
					}
					repaint();

				}

			}

		});

	}
	private void refreshProgressBars() {

		getTotalLabel().setText(""+bj.getTotalNumberOfJobs());

		getDoneProgress().setMaximum(bj.getTotalNumberOfJobs());
		int i = bj.getNumberOfSuccessfulJobs();
		getDoneProgress().setValue(i);
		if ( i > 0 ) {
			getDoneValueLabel().setText(""+i);
		} else {
			getDoneValueLabel().setText("");
		}

		getFailProgress().setMaximum(bj.getTotalNumberOfJobs());
		i = bj.getNumberOfFailedJobs();
		getFailProgress().setValue(i);
		if ( i > 0 ) {
			getFailValueLabel().setText(""+i);
		} else {
			getFailValueLabel().setText("");
		}

		getRunProgress().setMaximum(bj.getTotalNumberOfJobs());
		i = bj.getNumberOfRunningJobs();
		getRunProgress().setValue(i);
		if ( i > 0 ) {
			getRunValueLabel().setText(""+i);
		} else {
			getRunValueLabel().setText("");
		}

		getUnsubProgress().setMaximum(bj.getTotalNumberOfJobs());
		i = bj.getNumberOfUnsubmittedJobs();
		getUnsubProgress().setValue(i);
		if ( i > 0 ) {
			getUnsubValueLabel().setText(""+i);
		} else {
			getUnsubValueLabel().setText("");
		}

		getWaitingProgress().setMaximum(bj.getTotalNumberOfJobs());
		i = bj.getNumberOfWaitingJobs();
		getWaitingProgress().setValue(i);
		if ( i > 0 ) {
			getWaitingValueLabel().setText(""+i);
		} else {
			getWaitingValueLabel().setText("");
		}

	}
}
