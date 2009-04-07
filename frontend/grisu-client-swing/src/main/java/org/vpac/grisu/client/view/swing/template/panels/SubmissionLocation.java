package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.utils.QueueRenderer;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.fs.model.MountPoint;
import org.vpac.grisu.model.EnvironmentSnapshotValues;
import org.vpac.grisu.model.ResourceInformation;
import org.vpac.grisu.model.UserApplicationInformation;
import org.vpac.grisu.model.UserInformation;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SubmissionLocation extends JPanel implements TemplateNodePanel, ValueListener {
	
	private JComboBox queueComboBox;
	private JComboBox siteComboBox;
	private JLabel label_1;
	private JLabel label;
	static final Logger myLogger = Logger.getLogger(SubmissionLocation.class.getName());
	
	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	
	private TemplateNode templateNode;
	private String applicationName;
	
	private UserApplicationInformation infoObject = null;
	private Version versionPanel = null;
	
	Set<String> allSites = null;
	Set<String> allQueues = null;
	
	private final ResourceInformation resourceInfo = GrisuRegistry.getDefault().getResourceInformation();
	private EnvironmentSnapshotValues esv = GrisuRegistry.getDefault().getEnvironmentSnapshotValues();
	private final UserInformation userInformation = GrisuRegistry.getDefault().getUserInformation();

	private String lastSubmissionLocation = null;
	
	private DefaultTemplateNodeValueSetter stagingFsSetter = new DefaultTemplateNodeValueSetter();
	
	/**
	 * Create the panel
	 */
	public SubmissionLocation() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBorder(new TitledBorder(null, "Submission location", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		add(getLabel(), new CellConstraints(2, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getLabel_1(), new CellConstraints(2, 4, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getSiteComboBox(), new CellConstraints(4, 2));
		add(getQueueComboBox(), new CellConstraints(4, 4));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;

		this.applicationName = this.templateNode.getTemplate().getApplicationName();
		this.infoObject = GrisuRegistry.getDefault().getUserApplicationInformation(applicationName);
		
		try {
			GrisuRegistry.getDefault().getHistoryManager().setMaxNumberOfEntries(TemplateTagConstants.getGlobalLastQueueKey(infoObject.getApplicationName()),1);
			lastSubmissionLocation = GrisuRegistry.getDefault().getHistoryManager().getEntries(TemplateTagConstants.getGlobalLastQueueKey(infoObject.getApplicationName())).get(0);
		} catch (Exception e) {
			lastSubmissionLocation = null;
		}

		// this might be slightly dodgy. But it should always work if a Version template tag is present.
		versionPanel = (Version)(this.templateNode.getTemplate().getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).getTemplateNodeValueSetter());
		versionPanel.addValueListener(this);

		this.templateNode.getTemplate().getTemplateNodes().get(TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME).setTemplateNodeValueSetter(stagingFsSetter);

		
		if ( versionPanel.getCurrentValue() != null ) {
			valueChanged(versionPanel, versionPanel.getCurrentValue());
		}
		
		if ( lastSubmissionLocation != null ) {
			String lastSite = GrisuRegistry.getDefault().getResourceInformation().getSite(lastSubmissionLocation);
			if ( siteModel.getIndexOf(lastSite) >= 0 ) {
				siteModel.setSelectedItem(lastSite);
				if ( queueModel.getIndexOf(lastSubmissionLocation) >= 0 ) {
					queueModel.setSelectedItem(lastSubmissionLocation);
				}
			}
		}
		
	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {
		// version changed...
		myLogger.debug("SubmissionLocationPanel: Version changed to: "+newValue);
		
		
		String oldSite = (String)siteModel.getSelectedItem();
		siteModel.removeAllElements();		
		
		String oldQueue = (String)queueModel.getSelectedItem();

		if ( versionPanel.getMode() == Version.DEFAULT_VERSION_MODE ) {
			allQueues = new HashSet<String>();
			allQueues.add("Mode not supported yet.");
			allSites = new HashSet<String>();
			allSites.add("Mode not supported yet");
		} else if ( versionPanel.getMode() == Version.ANY_VERSION_MODE ) {
			allQueues = infoObject.getAvailableSubmissionLocationsForFqan(esv.getCurrentFqan());
		} else {
			allQueues = infoObject.getAvailableSubmissionLocationsForVersionAndFqan(newValue, esv.getCurrentFqan());
		}
		
		if ( versionPanel.getMode() != Version.DEFAULT_VERSION_MODE ) {
			// until the mode is supported
			allSites = resourceInfo.distillSitesFromSubmissionLocations(allQueues);
		}
		
		
		
		for ( String tempsite : allSites ) {
			siteModel.addElement(tempsite);
		}
		
		if ( oldSite != null && siteModel.getIndexOf(oldSite) >= 0 ) {
			changeToSite(oldSite);
		}
		
		if ( queueModel.getIndexOf(oldQueue) >= 0 ) {
			queueModel.setSelectedItem(oldQueue);
		}
		
	}
	
	private void changeToSite(String site) {

		siteModel.setSelectedItem(site);
		
	}
	
	private void repopulateQueueCombobox() {
		
		String oldQueue = (String)queueModel.getSelectedItem();
		queueModel.removeAllElements();

		String newSite = (String)siteModel.getSelectedItem();
		if ( newSite != null && !"".equals(newSite) ) {
		for ( String queue: resourceInfo.filterSubmissionLocationsForSite(newSite, allQueues) ) {
			queueModel.addElement(queue);
		}
		
		if ( oldQueue != null && queueModel.getIndexOf(oldQueue) >= 0 ) {
			queueModel.setSelectedItem(oldQueue);
		}
		}
		
	}
	
	public void templateNodeUpdated(TemplateNodeEvent event) {
		
	}

	public String getExternalSetValue() {
		return (String)queueModel.getSelectedItem();
	}

	public void setExternalSetValue(String value) {
		// TODO Auto-generated method stub
		
	}
	

	
	
	// event stuff
	// ========================================================

	private Vector<ValueListener> valueChangedListeners;

	private void fireSubmissionLocationChanged(String newValue) {

		myLogger.debug("Fire value changed event: new value: " + newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners
						.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<ValueListener> e = valueChangedTargets.elements();
			while (e.hasMoreElements()) {
				ValueListener valueChanged_l = (ValueListener) e.nextElement();
				valueChanged_l.valueChanged(this, newValue);
			}
		}
	}

	// register a listener
	synchronized public void addValueListener(ValueListener l) {
		if (valueChangedListeners == null)
			valueChangedListeners = new Vector<ValueListener>();
		valueChangedListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeValueListener(ValueListener l) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new Vector<ValueListener>();
		}
		valueChangedListeners.removeElement(l);
	}
	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("Site:");
		}
		return label;
	}
	/**
	 * @return
	 */
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setText("Queue:");
		}
		return label_1;
	}
	/**
	 * @return
	 */
	protected JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(siteModel);
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						repopulateQueueCombobox();
					}
					
				}
			});
		}
		return siteComboBox;
	}
	/**
	 * @return
	 */
	protected JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
			queueComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					
					String temp = ((String)queueModel.getSelectedItem());
					if ( temp != null && ! "".equals(temp) ) {
					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						fireSubmissionLocationChanged(temp);
					}
					MountPoint fs = userInformation.getRecommendedMountPoint(temp, esv.getCurrentFqan());
					stagingFsSetter.setExternalSetValue(fs.getRootUrl());
					myLogger.debug("Set staging fs to: "+fs);
					GrisuRegistry.getDefault().getHistoryManager().addHistoryEntry(TemplateTagConstants.getGlobalLastQueueKey(infoObject.getApplicationName()), (String)queueModel.getSelectedItem());
					}
				}
			});
			queueComboBox.setRenderer(new QueueRenderer(queueComboBox.getRenderer()));
		}
		return queueComboBox;
	}

}
