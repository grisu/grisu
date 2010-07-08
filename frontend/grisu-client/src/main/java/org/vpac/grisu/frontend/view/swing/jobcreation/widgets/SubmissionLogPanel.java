package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.model.job.JobObject;

import au.org.arcs.jcommons.constants.Constants;

public class SubmissionLogPanel extends JPanel implements
		PropertyChangeListener {
	private JScrollPane scrollPane;
	private JTextArea textArea;

	private JobObject currentJob = null;

	/**
	 * Create the panel.
	 */
	public SubmissionLogPanel() {
		setBorder(new TitledBorder(null, "Submission log",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}

	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setEditable(false);
		}
		return textArea;
	}

	public void setJobObject(JobObject jobObject) {

		if (currentJob != null) {
			currentJob.removePropertyChangeListener(this);
		}

		currentJob = jobObject;
		getTextArea().setText("");
		currentJob.addPropertyChangeListener(this);

	}

	private void appendText(final String msg) {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getTextArea().append(msg + "\n");
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		try {

			String oldValue = null;
			if (evt.getOldValue() != null) {
				try {
					oldValue = (String) evt.getOldValue();
				} catch (Exception e) {
				}
			}
			String newValue = null;
			if (evt.getNewValue() != null) {
				try {
					newValue = (String) evt.getNewValue();
				} catch (Exception e) {
					try {
						newValue = ((Integer) evt.getNewValue()).toString();
					} catch (Exception e1) {
					}
				}
			}

			String propName = evt.getPropertyName();

			String text = null;

			if ("submissionLog".equals(propName)) {
				List<String> log = (List<String>) evt.getNewValue();
				text = log.get(log.size() - 1);
			} else if (Constants.STATUS_STRING.equals(propName)) {
				return;
			} else if ("statusString".equals(propName)) {
				text = "New status: " + newValue;
			} else if (StringUtils.isBlank(oldValue)
					&& StringUtils.isNotBlank(newValue)) {
				text = "Set " + propName + ": " + newValue;
			} else if (StringUtils.isNotBlank(oldValue)
					&& StringUtils.isNotBlank(newValue)) {
				text = "Changed value for " + propName + ": " + oldValue
						+ " -> " + newValue;
			}
			if (StringUtils.isNotBlank(text)) {
				appendText(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setText(String string) {
		getTextArea().setText(string);
	}
}
