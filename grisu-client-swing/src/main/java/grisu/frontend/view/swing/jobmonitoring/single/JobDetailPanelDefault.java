package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.control.JobConstants;
import grisu.control.ServiceInterface;
import grisu.frontend.model.job.JobException;
import grisu.frontend.model.job.JobObject;
import grisu.frontend.view.swing.files.virtual.GridFileManagementPanel;
import grisu.frontend.view.swing.jobmonitoring.single.appSpecific.AppSpecificViewerPanel;
import grisu.frontend.view.swing.utils.BackgroundActionProgressDialogSmall;
import grisu.jcommons.constants.Constants;
import grisu.model.dto.DtoActionStatus;
import grisu.model.dto.GridFile;
import grisu.model.status.StatusObject;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jidesoft.swing.JideTabbedPane;

public class JobDetailPanelDefault extends JPanel implements
PropertyChangeListener, JobDetailPanel {

	static final Logger myLogger = LoggerFactory
			.getLogger(JobDetailPanelDefault.class.getName());

	private static String generateHtml(Map<String, String> jobProperties) {

		final StringBuffer html = new StringBuffer(
				"<html><table width=\"100%\">");

		boolean alternate = true;
		for (final String key : jobProperties.keySet()) {
			if (alternate) {
				html.append("<tr bgcolor=\"#FFFFFF\"><td>");
			} else {
				html.append("<tr><td>");
			}
			html.append(key);
			html.append("</td><td>");
			html.append(jobProperties.get(key));
			html.append("</td></tr>");
			alternate = !alternate;
		}
		html.append("</table></html>");

		return html.toString();
	}

	private static String generateLogHtml(Map<Date, String> jobProperties) {

		final StringBuffer html = new StringBuffer(
				"<html><table width=\"100%\">");

		boolean alternate = true;
		for (final Date key : jobProperties.keySet()) {
			if (alternate) {
				html.append("<tr bgcolor=\"#FFFFFF\"><td>");
			} else {
				html.append("<tr><td>");
			}
			html.append(key.toString());
			html.append("</td><td>");
			html.append(jobProperties.get(key));
			html.append("</td></tr>");
			alternate = !alternate;
		}
		html.append("</table></html>");

		return html.toString();
	}

	private final ImageIcon REFRESH_ICON = new ImageIcon(
			JobDetailPanelDefault.class.getClassLoader().getResource(
					"refresh.png"));

	private JTextField txtNa;
	private JobObject job;
	private JideTabbedPane jideTabbedPane;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JEditorPane logTextArea;

	private GridFileManagementPanel fileManagementPanel;
	private final ServiceInterface si;
	private JLabel lblApplication;
	private JLabel lblJobname;
	private JTextField jobnameTextField;
	private JTextField applicationTextField;
	private JLabel statusRefreshButton;
	private JLabel lblSubmitted;

	private JTextField submittedTextField;
	private AppSpecificViewerPanel asvp = null;

	// public static SimpleDateFormat format = new SimpleDateFormat(
	// "dd.MM.yyyy - HH.mm.SS");

	private JEditorPane propertiesPane;
	private JButton archiveButton;
	private JButton killButton;
	private JButton cleanButton;
	private JSeparator separator;
	private JButton btnRefresh;

	/**
	 * Create the panel.
	 * 
	 * @wbp.parser.constructor
	 */
	public JobDetailPanelDefault(ServiceInterface si) {

		this.si = si;

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(93dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(24dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(42dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("10dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLblJobname(), "2, 2, right, default");
		add(getJobnameTextField(), "4, 2, fill, default");
		add(getSeparator(), "6, 2, 1, 7, right, default");
		add(getArchiveButton(), "8, 2");
		add(getLblSubmitted(), "2, 4, right, default");
		add(getSubmittedTextField(), "4, 4, fill, default");
		add(getKillButton(), "8, 4");
		add(getLblApplication(), "2, 6, right, default");
		add(getApplicationTextField(), "4, 6, fill, default");
		add(getCleanButton(), "8, 6");
		add(getStatusRefreshButton(), "2, 8, right, default");
		add(getTxtNa(), "4, 8, fill, default");
		add(getBtnRefresh(), "8, 8");
		add(getJideTabbedPane(), "2, 10, 7, 1, fill, fill");

	}

	public JobDetailPanelDefault(ServiceInterface si, JobObject job) {
		this(si);
		setJob(job);
	}

	private JTextField getApplicationTextField() {
		if (applicationTextField == null) {
			applicationTextField = new JTextField();
			applicationTextField.setText("n/a");
			applicationTextField.setHorizontalAlignment(SwingConstants.CENTER);
			applicationTextField.setEditable(false);
			applicationTextField.setColumns(10);
		}
		return applicationTextField;
	}

	private JButton getArchiveButton() {
		if (archiveButton == null) {
			archiveButton = new JButton("Archive");
			archiveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (job == null) {
						return;
					}

					new Thread() {
						@Override
						public void run() {
							final BackgroundActionProgressDialogSmall d = new BackgroundActionProgressDialogSmall(
									"Archiving job:", job.getJobname());
							try {
								final String tmp = job.archive(null, false);

								final StatusObject so = StatusObject
										.waitForActionToFinish(si, tmp, 5,
												true);

								d.close();

								if (so.getStatus().isFailed()) {

									final String msg = "Can't archive job: "
											+ DtoActionStatus.getLastMessage(so
													.getStatus());
									final ErrorInfo info = new ErrorInfo(
											"Job archiving error", msg, null,
											"Error", null, Level.SEVERE, null);

									final JXErrorPane pane = new JXErrorPane();
									pane.setErrorInfo(info);

									JXErrorPane.showDialog(
											JobDetailPanelDefault.this
											.getRootPane(), pane);

								}

							} catch (final Exception e) {
								d.close();
								final ErrorInfo info = new ErrorInfo(
										"Job archiving error",
										"Can't archive job:\n\n"
												+ e.getLocalizedMessage(),
												null, "Error", e, Level.SEVERE, null);

								final JXErrorPane pane = new JXErrorPane();
								pane.setErrorInfo(info);

								JXErrorPane.showDialog(
										JobDetailPanelDefault.this
										.getRootPane(), pane);

							}

						}
					}.start();

				}
			});
		}
		return archiveButton;
	}

	private JButton getCleanButton() {
		if (cleanButton == null) {
			cleanButton = new JButton("Clean");
			cleanButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (job == null) {
						return;
					}

					new Thread() {
						@Override
						public void run() {

							final int n = JOptionPane.showConfirmDialog(
									JobDetailPanelDefault.this,
									"Cleaning job "
											+ JobDetailPanelDefault.this.job
											.getJobname() + ".",
											"Confirmation",
											JOptionPane.OK_CANCEL_OPTION);

							if (n == JOptionPane.CANCEL_OPTION) {
								return;
							}

							final BackgroundActionProgressDialogSmall d = new BackgroundActionProgressDialogSmall(
									"Cleaning job:", job.getJobname());
							try {
								job.kill(true);
								d.close();
							} catch (final Exception e) {
								d.close();
								final ErrorInfo info = new ErrorInfo(
										"Job cleaning error",
										"Can't clean job:\n\n"
												+ e.getLocalizedMessage(),
												null, "Error", e, Level.SEVERE, null);

								final JXErrorPane pane = new JXErrorPane();
								pane.setErrorInfo(info);

								JXErrorPane.showDialog(
										JobDetailPanelDefault.this
										.getRootPane(), pane);

							}

						}
					}.start();

				}
			});
		}
		return cleanButton;
	}


	private GridFileManagementPanel getFileManagementPanel() {
		if ( fileManagementPanel == null ) {
			List<GridFile> left = Lists.newArrayList();
			fileManagementPanel = new GridFileManagementPanel(si, left, null,
					false, true);
			fileManagementPanel.setRightPanelToPreview(true);
		}
		return fileManagementPanel;
	}

	private JideTabbedPane getJideTabbedPane() {
		if (jideTabbedPane == null) {
			jideTabbedPane = new JideTabbedPane();
			jideTabbedPane.setTabPlacement(SwingConstants.TOP);

			jideTabbedPane.addTab("Job directory", null,
					getFileManagementPanel(), null);
			jideTabbedPane.addTab("Properties", getScrollPane_1());
			jideTabbedPane.addTab("Log", getScrollPane_1_1());
		}
		return jideTabbedPane;
	}

	private JTextField getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new JTextField();
			jobnameTextField.setText("n/a");
			jobnameTextField.setHorizontalAlignment(SwingConstants.CENTER);
			jobnameTextField.setEditable(false);
			jobnameTextField.setColumns(10);
		}
		return jobnameTextField;
	}

	private JButton getKillButton() {
		if (killButton == null) {
			killButton = new JButton("Kill");
			killButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (job == null) {
						return;
					}

					new Thread() {
						@Override
						public void run() {

							final int n = JOptionPane.showConfirmDialog(
									JobDetailPanelDefault.this,
									"Killing job "
											+ JobDetailPanelDefault.this.job
											.getJobname() + ".",
											"Confirmation",
											JOptionPane.OK_CANCEL_OPTION);

							if (n == JOptionPane.CANCEL_OPTION) {
								return;
							}

							final BackgroundActionProgressDialogSmall d = new BackgroundActionProgressDialogSmall(
									"Killing job:", job.getJobname());
							try {
								job.kill(false);
								d.close();
							} catch (final JobException e) {
								d.close();
								final ErrorInfo info = new ErrorInfo(
										"Job kill error", "Can't kill job:\n\n"
												+ e.getLocalizedMessage(),
												null, "Error", e, Level.SEVERE, null);

								final JXErrorPane pane = new JXErrorPane();
								pane.setErrorInfo(info);

								JXErrorPane.showDialog(
										JobDetailPanelDefault.this
										.getRootPane(), pane);

							}

						}
					}.start();

				}
			});
		}
		return killButton;
	}

	private JLabel getLblApplication() {
		if (lblApplication == null) {
			lblApplication = new JLabel("Application");
		}
		return lblApplication;
	}

	// private JTextArea getPropertiesTextArea() {
	// if (propertiesTextArea == null) {
	// propertiesTextArea = new JTextArea();
	// }
	// return propertiesTextArea;
	// }

	private JLabel getLblJobname() {
		if (lblJobname == null) {
			lblJobname = new JLabel("Jobname");
		}
		return lblJobname;
	}

	private JLabel getLblSubmitted() {
		if (lblSubmitted == null) {
			lblSubmitted = new JLabel("Submitted");
		}
		return lblSubmitted;
	}

	private JEditorPane getLogTextArea() {
		if (logTextArea == null) {
			logTextArea = new JEditorPane();
			logTextArea.setContentType("text/html");
			logTextArea.setEditable(false);
		}
		return logTextArea;
	}

	public JPanel getPanel() {
		return this;
	}

	private JEditorPane getPropertiesPane() {
		if (propertiesPane == null) {
			propertiesPane = new JEditorPane();
			propertiesPane.setContentType("text/html");
			propertiesPane.setEditable(false);
		}
		return propertiesPane;
	}

	private JScrollPane getScrollPane_1() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getPropertiesPane());
		}
		return scrollPane;
	}

	private JScrollPane getScrollPane_1_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getLogTextArea());
		}
		return scrollPane_1;
	}

	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
			separator.setOrientation(SwingConstants.VERTICAL);
		}
		return separator;
	}

	private JLabel getStatusRefreshButton() {
		if (statusRefreshButton == null) {
			statusRefreshButton = new JLabel("Status");
			statusRefreshButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {

					refreshJob();

				}

				@Override
				public void mouseEntered(MouseEvent e) {

					statusRefreshButton.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

				}

				@Override
				public void mouseExited(MouseEvent e) {
					statusRefreshButton.setCursor(Cursor.getDefaultCursor());
				}

			});
			statusRefreshButton.setBorder(null);
//			statusRefreshButton.setIcon(REFRESH_ICON);
		}
		return statusRefreshButton;
	}

	private JTextField getSubmittedTextField() {
		if (submittedTextField == null) {
			submittedTextField = new JTextField();
			submittedTextField.setHorizontalAlignment(SwingConstants.CENTER);
			submittedTextField.setText("n/a");
			submittedTextField.setEditable(false);
			submittedTextField.setColumns(10);
		}
		return submittedTextField;
	}

	private JTextField getTxtNa() {
		if (txtNa == null) {
			txtNa = new JTextField();
			txtNa.setText("n/a");
			txtNa.setHorizontalAlignment(SwingConstants.CENTER);
			txtNa.setEditable(false);
			txtNa.setColumns(10);
		}
		return txtNa;
	}

	public void propertyChange(final PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals("status")) {

			SwingUtilities.invokeLater(new Thread() {
				@Override
				public void run() {
					getTxtNa().setText(
							JobConstants.translateStatus((Integer) (evt
									.getNewValue())));
					setLog();
					setProperties();
					final int status = (Integer) evt.getNewValue();
					if ((status > JobConstants.READY_TO_SUBMIT)) {
						getFileManagementPanel().refresh();

						if (status >= JobConstants.FINISHED_EITHER_WAY) {

							getKillButton().setEnabled(false);
							getCleanButton().setEnabled(true);
							getArchiveButton().setEnabled(true);

						}

					}
				}
			});


		}
	}

	public void setJob(JobObject job) {

		this.job = job;

		this.job.addPropertyChangeListener(this);

		// try to create app specific panel
		asvp = AppSpecificViewerPanel.create(si, job);
		if (asvp != null) {
			try {
				asvp.setJob(job);
				getJideTabbedPane().insertTab(
						job.getApplication() + " details", null, asvp, null, 0);
			} catch (final Exception e) {
				myLogger.error(e.getLocalizedMessage(), e);
			}
		}

		getJideTabbedPane().setSelectedIndex(0);
		getStatusRefreshButton().setEnabled(true);
		getJobnameTextField().setText(job.getJobname());

		getFileManagementPanel().setRootUrl(job.getJobDirectoryUrl());
		getTxtNa().setText(JobConstants.translateStatus(job.getStatus(false)));

		final int status = job.getStatus(false);
		if (status <= JobConstants.ACTIVE) {
			getKillButton().setEnabled(true);
			getCleanButton().setEnabled(true);
			getArchiveButton().setEnabled(false);
		} else {
			getKillButton().setEnabled(false);
			getCleanButton().setEnabled(true);
			getArchiveButton().setEnabled(true);
		}

		setProperties();
		setLog();

	}

	private void setLog() {

		final String html = generateLogHtml(job.getLogMessages(true));

		getLogTextArea().setText(html);
	}

	private void setProperties() {
		final String propText = generateHtml(job.getAllJobProperties(true));
		getPropertiesPane().setText(propText);

		String subTime = null;
		try {
			final String subTimeString = job.getJobProperty(
					Constants.SUBMISSION_TIME_KEY, false);
			subTime = DateFormat.getInstance().format(
					new Date(Long.parseLong(subTimeString)));
		} catch (final Exception e) {
			subTime = "n/a";
		}
		getSubmittedTextField().setText(subTime);

		getApplicationTextField().setText(
				job.getJobProperty(Constants.APPLICATIONNAME_KEY, false));

	}
	
	private synchronized void refreshJob() {
		if (job != null) {
			new Thread() {
				@Override
				public void run() {
					SwingUtilities.invokeLater(new Thread() {
						@Override
						public void run() {
							getStatusRefreshButton().setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							getBtnRefresh().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							getBtnRefresh().setEnabled(false);
							getTxtNa().setText("Getting status...");
						}
					});

					try {
						job.getStatus(true);
					} finally {
						SwingUtilities.invokeLater(new Thread() {
							@Override
							public void run() {
								getStatusRefreshButton().setCursor(Cursor
										.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								getBtnRefresh().setCursor(Cursor
										.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								getTxtNa().setText(
										job.getStatusString(false));
								getBtnRefresh().setEnabled(true);
							}
						});

					}
				}
			}.start();
			getFileManagementPanel().refresh(true);
		}
	}
	
	private JButton getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new JButton("Refresh");
			btnRefresh.setIcon(REFRESH_ICON);
			btnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					refreshJob();
				}
			});
		}
		return btnRefresh;
	}
}
