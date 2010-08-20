package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.frontend.model.job.BackendException;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobsException;

public class SingleJobResubmitDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SingleJobResubmitDialog dialog = new SingleJobResubmitDialog(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SingleJobResubmitPanel singleJobResubmitPanel;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public SingleJobResubmitDialog(final BatchJobObject bj, final Set<String> jobnames) {
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
			JPanel buttonPane = new JPanel();
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
								setCursor(Cursor.getDefaultCursor().getPredefinedCursor(Cursor.WAIT_CURSOR));
								try {
									bj.restart(jobnames, singleJobResubmitPanel.getSubmissionLocations(), false);
								} catch (JobsException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (BackendException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
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
