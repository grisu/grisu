package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class BatchJobDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BatchJobDialog dialog = new BatchJobDialog(null, null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static BatchJobDialog open(ServiceInterface si, BatchJobObject bj) {

		final BatchJobDialog dialog = new BatchJobDialog(si, bj);

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				dialog.setVisible(true);
			}
		});

		return dialog;
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
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
