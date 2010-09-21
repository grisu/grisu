package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.SimpleSingleJobsGrid;

public class BatchJobSubJobsGrid extends SimpleSingleJobsGrid implements
		PropertyChangeListener {

	private static final long serialVersionUID = -1811967498034047862L;

	private final BatchJobObject bj;

	private JMenuItem mntmRestartSelectedJobs;

	public BatchJobSubJobsGrid(ServiceInterface si, BatchJobObject bj) {
		super(si, bj.getJobs());
		this.bj = bj;
		this.bj.addPropertyChangeListener(this);

		getPopupMenu().add(getMntmRestartSelectedJobs());

		if (this.bj.isResubmitting()) {
			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					getTable().setEnabled(false);
				}
			});
		}

	}

	private JMenuItem getMntmRestartSelectedJobs() {
		if (mntmRestartSelectedJobs == null) {
			mntmRestartSelectedJobs = new JMenuItem("Restart selected job(s)");
			mntmRestartSelectedJobs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					final Set<String> selectedJobs = new HashSet<String>();

					for (final JobObject job : getSelectedJobs()) {
						selectedJobs.add(job.getJobname());
					}

					final SingleJobResubmitDialog dialog = new SingleJobResubmitDialog(
							bj, selectedJobs);
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

				}
			});
		}
		return mntmRestartSelectedJobs;
	}

	@Override
	protected void killSelectedJobs(final boolean clean) {

		new Thread() {
			@Override
			public void run() {
				BatchJobSubJobsGrid.super.killSelectedJobs(clean);
				bj.refresh(false);
			}
		}.start();

	}

	public void propertyChange(PropertyChangeEvent evt) {

		if (BatchJobObject.RESUBMITTING.equals(evt.getPropertyName())) {
			if ((Boolean) evt.getNewValue()) {
				getTable().setEnabled(false);
			} else {
				getTable().setEnabled(true);
			}
		}
	}

	@Override
	protected void setDefaultColumns() {

		getTable().getColumnExt("Site").setVisible(true);
		getTable().getColumnExt("Application").setVisible(false);
		getTable().getColumnExt("Queue").setVisible(false);
		getTable().getColumnExt("Submission time").setVisible(false);
		getTable().getColumnExt("Group").setVisible(false);
		getTable().getColumnExt("Status").setVisible(true);

	}

}
