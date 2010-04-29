package org.vpac.grisu.frontend.view.swing.jobcreation;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXErrorPane;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TemplateWrapperPanel extends JPanel {

	public static final String JOB_CREATE_PANEL = "jobCreatePanel";
	public static final String SUBMISSION_LOG_PANEL = "logPanel";

	private final SubmissionMonitorPanel monitorPanel = new SubmissionMonitorPanel();
	private final JPanel creationPanel;

	private final CardLayout cardLayout = new CardLayout();

	private final TemplateObject template;
	private JButton submitButton;

	/**
	 * Create the panel.
	 */
	public TemplateWrapperPanel(TemplateObject template) {

		this.template = template;
		setLayout(cardLayout);

		creationPanel = new JPanel();

		add(creationPanel, JOB_CREATE_PANEL);
		creationPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		creationPanel.add(template.getTemplatePanel(), "2, 2, fill, fill");
		creationPanel.add(getSubmitButton(), "2, 4, right, default");
		add(monitorPanel, SUBMISSION_LOG_PANEL);

		cardLayout.show(this, JOB_CREATE_PANEL);
		monitorPanel.setTemplateWrapperPanel(this);

	}

	private JButton getSubmitButton() {
		if (submitButton == null) {
			submitButton = new JButton("Submit");
			submitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					try {
						JobObject job = JobObject.createJobObject(template
								.getServiceInterface(), template
								.getJobSubmissionObject());
						monitorPanel.startJobSubmission(job);
					} catch (JobPropertiesException e) {

						JXErrorPane.showDialog(e);

						return;
					}

				}
			});
		}
		return submitButton;
	}

	public void switchToJobCreationPanel() {
		cardLayout.show(this, JOB_CREATE_PANEL);
	}

	public void switchToLogPanel() {
		cardLayout.show(this, SUBMISSION_LOG_PANEL);
	}
}
