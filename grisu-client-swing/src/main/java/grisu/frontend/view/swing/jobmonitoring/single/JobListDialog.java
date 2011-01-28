package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.control.ServiceInterface;
import grisu.frontend.control.jobMonitoring.RunningJobManager;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;


public class JobListDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			final JobListDialog dialog = new JobListDialog(null, null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void open(final ServiceInterface si, final String application) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				final JobListDialog dialog = new JobListDialog(si, application);
				dialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosed(WindowEvent e) {

						RunningJobManager.getDefault(si).stopUpdate();
					}

				});
				dialog.setVisible(true);
			}
		});

	}

	private final JPanel contentPanel = new JPanel();
	private SingleJobTabbedPane singleJobTabbedPane;

	private final ServiceInterface si;
	private final String application;

	/**
	 * Create the dialog.
	 */
	public JobListDialog(ServiceInterface si, String application) {
		this.si = si;
		this.application = application;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 852, 638);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(getSingleJobTabbedPane(), BorderLayout.CENTER);
	}

	private SingleJobTabbedPane getSingleJobTabbedPane() {
		if (singleJobTabbedPane == null) {
			singleJobTabbedPane = new SingleJobTabbedPane(si, application);
		}
		return singleJobTabbedPane;
	}
}
