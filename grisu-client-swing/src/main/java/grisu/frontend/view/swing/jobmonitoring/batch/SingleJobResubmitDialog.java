package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.frontend.model.job.BackendException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobsException;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

public class SingleJobResubmitDialog extends JDialog {

	static final Logger myLogger = Logger
			.getLogger(SingleJobResubmitDialog.class.getName());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final SingleJobResubmitDialog dialog = new SingleJobResubmitDialog(
					null, null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private SingleJobResubmitPanel singleJobResubmitPanel;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public SingleJobResubmitDialog(final BatchJobObject bj,
			final Set<String> jobnames) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			singleJobResubmitPanel = new SingleJobResubmitPanel(bj, jobnames);
			contentPanel.add(singleJobResubmitPanel);
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			final JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttonPane.add(cancelButton);

			{
				final JButton okButton = new JButton("Resubmit");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						new Thread() {
							@Override
							public void run() {
								okButton.setEnabled(false);
								cancelButton.setEnabled(false);
								setCursor(Cursor
										.getDefaultCursor()
										.getPredefinedCursor(Cursor.WAIT_CURSOR));
								try {
									bj.restart(jobnames, singleJobResubmitPanel
											.getSubmissionLocations(), false);
								} catch (final JobsException e) {
									myLogger.error(e);
								} catch (final BackendException e) {
									myLogger.error(e);
								} catch (final InterruptedException e) {
									myLogger.error(e);
								}
								dispose();
							}
						}.start();

					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
