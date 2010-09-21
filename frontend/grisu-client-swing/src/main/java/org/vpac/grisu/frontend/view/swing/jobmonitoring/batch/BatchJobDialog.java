package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.jobMonitoring.RunningJobManager;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final BatchJobDialog dialog = new BatchJobDialog(null, null);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void open(final ServiceInterface si, final String batchJobName)
			throws NoSuchJobException {

		final BatchJobObject bj = RunningJobManager.getDefault(si).getBatchJob(
				batchJobName);

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				final BatchJobDialog dialog = new BatchJobDialog(si, bj);
				dialog.setVisible(true);
			}
		});

	}

	private final ServiceInterface si;

	private final BatchJobObject bj;
	private final JPanel contentPanel = new JPanel();

	private BatchJobPanel batchJobPanel;

	/**
	 * Create the dialog.
	 */
	public BatchJobDialog(ServiceInterface si, BatchJobObject bj) {
		this.si = si;
		this.bj = bj;
		setModal(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getBatchJobPanel(), BorderLayout.CENTER);
	}

	private BatchJobPanel getBatchJobPanel() {
		if (batchJobPanel == null) {
			batchJobPanel = new BatchJobPanel(si, bj);
		}
		return batchJobPanel;
	}
}
