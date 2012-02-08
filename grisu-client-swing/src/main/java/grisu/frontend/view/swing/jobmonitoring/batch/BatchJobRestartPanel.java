package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.frontend.model.job.BackendException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobsException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BatchJobRestartPanel extends JPanel implements
		PropertyChangeListener {

	static final Logger myLogger = LoggerFactory
			.getLogger(BatchJobRestartPanel.class.getName());

	private final BatchJobObject bj;
	private JButton failedJobsButton;
	private JButton waitingJobsButton;
	private JButton failedWaitingJobsButton;

	/**
	 * Create the panel.
	 */
	public BatchJobRestartPanel(BatchJobObject bj) {
		setBorder(new TitledBorder(null, "Resubmission", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		this.bj = bj;
		this.bj.addPropertyChangeListener(this);
		if (this.bj.isResubmitting()) {
			getWaitingButton().setEnabled(false);
			getWaitingFailedButton().setEnabled(false);
			getFailedJobsButton().setEnabled(false);
		}
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getFailedJobsButton(), "2, 2, fill, default");
		add(getWaitingButton(), "2, 4");
		add(getWaitingFailedButton(), "2, 6");

	}

	private JButton getFailedJobsButton() {
		if (failedJobsButton == null) {
			failedJobsButton = new JButton("Failed Jobs");
			failedJobsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						@Override
						public void run() {
							try {
								bj.restart(true, false, false, false);
								bj.refresh(false);
							} catch (final Throwable ex) {
								myLogger.error(ex.getLocalizedMessage(), ex);
							}
						}
					}.start();
				}
			});
		}
		return failedJobsButton;
	}

	private JButton getWaitingButton() {
		if (waitingJobsButton == null) {
			waitingJobsButton = new JButton("Waiting Jobs");
			waitingJobsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						@Override
						public void run() {
							try {
								bj.restart(false, true, true, false);
							} catch (final JobsException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							} catch (final BackendException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							} catch (final InterruptedException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							}
							bj.refresh(false);
						}
					}.start();
				}
			});
		}
		return waitingJobsButton;
	}

	private JButton getWaitingFailedButton() {
		if (failedWaitingJobsButton == null) {
			failedWaitingJobsButton = new JButton("Failed & Waiting");
			failedWaitingJobsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						@Override
						public void run() {
							try {
								bj.restart(true, true, true, false);
							} catch (final JobsException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							} catch (final BackendException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							} catch (final InterruptedException e) {
								myLogger.error(e.getLocalizedMessage(), e);
							}
							bj.refresh(false);
						}
					}.start();
				}
			});
		}
		return failedWaitingJobsButton;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		if (BatchJobObject.RESUBMITTING.equals(evt.getPropertyName())) {

			if ((Boolean) evt.getNewValue()) {
				getWaitingButton().setEnabled(false);
				getWaitingFailedButton().setEnabled(false);
				getFailedJobsButton().setEnabled(false);
			} else {
				getWaitingButton().setEnabled(true);
				getWaitingFailedButton().setEnabled(true);
				getFailedJobsButton().setEnabled(true);
			}
		}

	}
}
