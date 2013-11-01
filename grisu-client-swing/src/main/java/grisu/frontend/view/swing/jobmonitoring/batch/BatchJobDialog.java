package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

        throw new RuntimeException("Batchjobs not implemented anymore.");

//		final BatchJobObject bj = RunningJobManagerOld.getDefault(si).getBatchJob(
//				batchJobName);
//
//		EventQueue.invokeLater(new Runnable() {
//
//			public void run() {
//
//				final BatchJobDialog dialog = new BatchJobDialog(si, bj);
//				dialog.setVisible(true);
//			}
//		});

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
