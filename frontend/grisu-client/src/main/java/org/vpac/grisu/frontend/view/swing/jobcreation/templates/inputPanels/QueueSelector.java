package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.exceptions.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.helperPanels.HidingQueueInfoPanel;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class QueueSelector extends AbstractInputPanel {
	private JLabel lblQueue;
	private JComboBox queueComboBox;
	private JPanel panel;
	private HidingQueueInfoPanel hidingQueueInfoPanel;

	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();

	private SortedSet<GridResource> currentQueues = null;

	private String lastApplication = null;

	private Thread loadThread;

	private String lastSubLoc = null;

	public QueueSelector(String templateName, PanelConfig config)
			throws TemplateException {
		super(templateName, config);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLblQueue(), "2, 2, right, default");
		add(getQueueComboBox(), "4, 2, fill, default");
		// add(getHidingQueueInfoPanel(), "2, 4, 3, 1, fill, fill");
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {
		Map<String, String> defaultProperties = new HashMap<String, String>();

		return defaultProperties;
	}

	@Override
	public JComboBox getJComboBox() {
		return null;
	}

	@Override
	public JTextComponent getTextComponent() {
		return null;
	}

	@Override
	protected String getValueAsString() {
		return null;
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {

		if (Constants.SUBMISSIONLOCATION_KEY.equals(e.getPropertyName())) {
			return;
		}
		
		if (Constants.COMMANDLINE_KEY.equals(e.getPropertyName())) {
			String temp = getJobSubmissionObject().getApplication();
			if (temp == null) {
				if (lastApplication == null) {
					return;
				}
			} else {
				if (temp.equals(lastApplication)) {
					return;
				}
			}
		}

		loadQueues();
	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties)
			throws TemplateException {

	}

	@Override
	void setInitialValue() throws TemplateException {

		loadQueues();

	}

	private void loadQueues() {

		if (loadThread != null && loadThread.isAlive()) {
			// I know, I know. But I think it's ok in this case.
			loadThread.interrupt();
		}

		loadThread = new Thread() {
			public void run() {

				setLoading(true);
				String applicationName = getJobSubmissionObject()
						.getApplication();
				if (StringUtils.isBlank(applicationName)) {
					applicationName = Constants.GENERIC_APPLICATION_NAME;
				}
				ApplicationInformation ai = GrisuRegistryManager.getDefault(
						getServiceInterface()).getApplicationInformation(
						applicationName);

				// currentQueues = ai.getBestSubmissionLocations(
				// getJobSubmissionObject().getJobSubmissionPropertyMap(),
				// GrisuRegistryManager.getDefault(getServiceInterface())
				// .getUserEnvironmentManager().getCurrentFqan());
				currentQueues = ai.getAllSubmissionLocationsAsGridResources(
						getJobSubmissionObject().getJobSubmissionPropertyMap(),
						GrisuRegistryManager.getDefault(getServiceInterface())
								.getUserEnvironmentManager().getCurrentFqan());
				setLoading(false);
				queueModel.removeAllElements();
				for (GridResource gr : currentQueues) {
					queueModel.addElement(gr);
				}
			}
		};

		loadThread.start();

	}

	@Override
	protected void templateRefresh(JobSubmissionObjectImpl jobObject) {

		if (useHistory()) {
			addValueToHistory();
		}

	}

	private void setLoading(boolean loading) {

		if (loading) {
			queueModel.removeAllElements();
			queueModel.addElement("Calculating...");
		}

		getQueueComboBox().setEnabled(!loading);
		getHidingQueueInfoPanel().setLoading(loading);

	}

	private JLabel getLblQueue() {
		if (lblQueue == null) {
			lblQueue = new JLabel("Submit to:");
		}
		return lblQueue;
	}

	private JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
			queueComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {

					GridResource gr;
					try {
						gr = (GridResource) (queueModel.getSelectedItem());
						if (gr == null) {
							return;
						}
					} catch (Exception ex) {
						return;
					}
					String subLoc = SubmissionLocationHelpers
							.createSubmissionLocationString(gr);

					if (subLoc.equals(lastSubLoc)) {
						return;
					}
					lastSubLoc = subLoc;

					try {
						setValue("submissionLocation", subLoc);
					} catch (TemplateException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return queueComboBox;
	}

	private HidingQueueInfoPanel getHidingQueueInfoPanel() {
		if (hidingQueueInfoPanel == null) {
			hidingQueueInfoPanel = new HidingQueueInfoPanel();
		}
		return hidingQueueInfoPanel;
	}
}
