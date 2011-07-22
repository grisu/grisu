package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobObject;
import grisu.jcommons.constants.Constants;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeWillExpandListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class SubmissionLogPanel extends JPanel implements
PropertyChangeListener {

	static final Logger myLogger = Logger
			.getLogger(TreeWillExpandListener.class.getName());

	private JScrollPane scrollPane;
	private JTextArea textArea;

	private JobObject currentJob = null;
	private BatchJobObject currentBatchJobObject = null;

	/**
	 * Create the panel.
	 */
	public SubmissionLogPanel() {
		setBorder(new TitledBorder(null, "Submission log",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);

	}

	public void appendMessage(String message) {

		getTextArea().append(message);
		getTextArea().setCaretPosition(getTextArea().getText().length());

	}

	private void appendText(final String msg) {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getTextArea().append(msg + "\n");
			}
		});
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
			textArea.setMargin(new Insets(5, 5, 5, 5));
		}
		return textArea;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		try {

			String oldValue = null;
			if (evt.getOldValue() != null) {
				try {
					oldValue = (String) evt.getOldValue();
				} catch (final Exception e) {
				}
			}
			String newValue = null;
			if (evt.getNewValue() != null) {
				try {
					newValue = (String) evt.getNewValue();
				} catch (final Exception e) {
					try {
						newValue = ((Integer) evt.getNewValue()).toString();
					} catch (final Exception e1) {
					}
				}
			}

			final String propName = evt.getPropertyName();

			String text = null;

			if ("submissionLog".equals(propName)) {
				final List<String> log = (List<String>) evt.getNewValue();
				text = log.get(log.size() - 1);
			} else if (Constants.STATUS_STRING.equals(propName)) {
				return;
			} else if ("statusString".equals(propName)) {
				return;
				// text = "New status: " + newValue;
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
		} catch (final Exception e) {
			myLogger.error(e);
		}
	}

	public void setBatchJobOject(BatchJobObject bjo) {
		if (currentJob != null) {
			currentJob.removePropertyChangeListener(this);
		}
		currentJob = null;

		if (currentBatchJobObject != null) {
			currentBatchJobObject.removePropertyChangeListener(this);
		}

		currentBatchJobObject = bjo;
		setText("");
		currentBatchJobObject.addPropertyChangeListener(this);
	}

	public void setJobObject(JobObject jobObject) {

		if (currentJob != null) {
			currentJob.removePropertyChangeListener(this);
		}

		if (currentBatchJobObject != null) {
			currentBatchJobObject.removePropertyChangeListener(this);
		}
		currentBatchJobObject = null;

		currentJob = jobObject;
		setText("");
		currentJob.addPropertyChangeListener(this);

	}

	public void setText(final String string) {
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getTextArea().setText(string);
				getTextArea()
				.setCaretPosition(getTextArea().getText().length());
			}
		});
	}
}
