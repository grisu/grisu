package org.vpac.grisu.client.view.swing.template.modules.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.ApplicationInfoObject;
import org.vpac.grisu.client.model.ModeNotSupportedException;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class VersionQueuePanel extends JPanel implements ActionListener {
	
	static final Logger myLogger = Logger.getLogger(VersionQueuePanel.class
			.getName());
	
	
	
	public static final String ANY_MODE_STRING = "Any";
	public static final String DEFAULT_MODE_STRING = "Default";
	public static final String EXACT_MODE_STRING = "Exact";
	
	public static final String ANY_MODE_TEMPLATETAG_KEY = "useAny";
	public static final String DEFAULT_MODE_TEMPLATETAG_KEY = "useDefault";
	public static final String EXACT_MODE_TEMPLATETAG_KEY = "useExact";
	
	public static final String STARTUP_MODE_KEY = "startMode";

	private JComboBox queueComboBox;
	private JComboBox siteComboBox;
	private JLabel queueLabel;
	private JLabel siteLabel;
	private JPanel subLocPanel;
	private JComboBox versionComboBox;
	private JRadioButton exactRadioButton;
	private JRadioButton anyRadioButton;
	private JRadioButton defaultRadioButton;
	private JPanel versionPanel;
	
	private ApplicationInfoObject infoObject = null;
	
	private EnvironmentManager em = null;
	private final String application;
	private List<String> versions = null;
	
	private DefaultComboBoxModel versionModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	
	private boolean anyModeSupported = true;
	private boolean defaultModeSupported = true;
	private boolean exactModeSupported = true;
	
	private int currentMode = -1;

	private ButtonGroup modeGroup = new ButtonGroup();
	
	private TemplateNode versionNode = null;
	private TemplateNode hostnameNode = null;
	
	/**
	 * Create the panel
	 */
	public VersionQueuePanel(EnvironmentManager em, TemplateNode versionNode, TemplateNode hostnameNode, String application) {
		
		super();
		this.em = em;
		this.versionNode = versionNode;
		this.hostnameNode = hostnameNode;
		this.application = application;
		
		this.anyModeSupported = versionNode.getOtherProperties().containsKey(ANY_MODE_TEMPLATETAG_KEY);
		this.defaultModeSupported = versionNode.getOtherProperties().containsKey(DEFAULT_MODE_TEMPLATETAG_KEY);
		this.exactModeSupported = versionNode.getOtherProperties().containsKey(EXACT_MODE_TEMPLATETAG_KEY);
		
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("19dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("58dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("33dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("29dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		
		add(getVersionPanel(), new CellConstraints(2, 2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getSubLocPanel(), new CellConstraints(2, 4, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		//
		this.infoObject = new ApplicationInfoObject(em, application, this.currentMode);
		
		for ( String version : em.getAllAvailableVersionsForApplication(application) ) {
			versionModel.addElement(version);
		}

		// set startup mode
		String startUpMode = versionNode.getOtherProperties().get(STARTUP_MODE_KEY);
		
		if ( startUpMode == null || startUpMode.equals("") ) {
			startUpMode = ANY_MODE_STRING;
		}
		
		if ( startUpMode.equals(ANY_MODE_STRING) ) {
			if ( ! anyModeSupported ) {
				// do nothing
				myLogger.error("useAny not specified. Using any.");
				getAnyRadioButton().setSelected(true);
				return;
			} else {
				// select any mode
				getAnyRadioButton().setSelected(true);
			}
		} else if ( startUpMode.equals(DEFAULT_MODE_STRING) ) {
			if ( ! defaultModeSupported ) {
				// do nothing
				myLogger.error("useDefault not specified. Using any.");
				getAnyRadioButton().setSelected(true);
				return;
			} else {
				// select default mode
				getDefaultRadioButton().setSelected(true);
			}
		} else if ( startUpMode.equals(EXACT_MODE_STRING) ) {
			if ( ! defaultModeSupported ) {
				// do nothing
				myLogger.error("useExact not specified. Using any.");
				getAnyRadioButton().setSelected(true);
				return;
			} else {
				// select exact mode
				getExactRadioButton().setSelected(true);
			}
		} else {
			// do nothing
			myLogger.error("Mode "+startUpMode+" not supported. Using any.");
			getAnyRadioButton().setSelected(true);
		}
					
	}
	/**
	 * @return
	 */
	protected JPanel getVersionPanel() {
		if (versionPanel == null) {
			versionPanel = new JPanel();
			versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.X_AXIS));
			versionPanel.setFocusCycleRoot(true);
			versionPanel.setBorder(new TitledBorder(null, "Version", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			if ( anyModeSupported ) {
				versionPanel.add(getAnyRadioButton());
				modeGroup.add(getAnyRadioButton());
				this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
			}
			if ( defaultModeSupported ) {
				versionPanel.add(getDefaultRadioButton());
				modeGroup.add(getDefaultRadioButton());
				if ( this.currentMode == -1 ) {
					this.currentMode = ApplicationInfoObject.DEFAULT_VERSION_MODE;
				}
			}
			if ( exactModeSupported ) {
				versionPanel.add(getExactRadioButton());
				modeGroup.add(getExactRadioButton());
				if ( this.currentMode == -1 ) {
					this.currentMode = ApplicationInfoObject.EXACT_VERSION_MODE;
				}
			}
			versionPanel.add(getVersionComboBox());
		}
		return versionPanel;
	}
	/**
	 * @return
	 */
	protected JRadioButton getDefaultRadioButton() {
		if (defaultRadioButton == null) {
			defaultRadioButton = new JRadioButton();
			defaultRadioButton.setText(DEFAULT_MODE_STRING);
			defaultRadioButton.addActionListener(this);
			defaultRadioButton.setActionCommand(DEFAULT_MODE_STRING);
		}
		return defaultRadioButton;
	}
	/**
	 * @return
	 */
	protected JRadioButton getAnyRadioButton() {
		if (anyRadioButton == null) {
			anyRadioButton = new JRadioButton();
			anyRadioButton.setText(ANY_MODE_STRING);
			anyRadioButton.addActionListener(this);
			anyRadioButton.setActionCommand(ANY_MODE_STRING);
		}
		return anyRadioButton;
	}
	/**
	 * @return
	 */
	protected JRadioButton getExactRadioButton() {
		if (exactRadioButton == null) {
			exactRadioButton = new JRadioButton();
			exactRadioButton.setText(EXACT_MODE_STRING);
			exactRadioButton.addActionListener(this);
			exactRadioButton.setActionCommand(EXACT_MODE_STRING);
		}
		return exactRadioButton;
	}
	/**
	 * @return
	 */
	protected JComboBox getVersionComboBox() {
		if (versionComboBox == null) {
			versionComboBox = new JComboBox(versionModel);
			versionComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					
					infoObject.setVersion((String)versionModel.getSelectedItem());
					if ( versionModel.getSize() > 0 ) {
						fillSiteCombobox();
					}
					
				}
			});
		}
		return versionComboBox;
	}
	/**
	 * @return
	 */
	protected JPanel getSubLocPanel() {
		if (subLocPanel == null) {
			subLocPanel = new JPanel();
			subLocPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("37dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("33dlu:grow(1.0)")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC}));
			subLocPanel.setBorder(new TitledBorder(null, "Submission location", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			subLocPanel.add(getSiteLabel(), new CellConstraints(1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			subLocPanel.add(getQueueLabel(), new CellConstraints(1, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			subLocPanel.add(getSiteComboBox(), new CellConstraints(3, 1));
			subLocPanel.add(getQueueComboBox(), new CellConstraints(3, 3));
		}
		return subLocPanel;
	}
	/**
	 * @return
	 */
	protected JLabel getSiteLabel() {
		if (siteLabel == null) {
			siteLabel = new JLabel();
			siteLabel.setText("Site:");
		}
		return siteLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getQueueLabel() {
		if (queueLabel == null) {
			queueLabel = new JLabel();
			queueLabel.setText("Queue:");
		}
		return queueLabel;
	}
	/**
	 * @return
	 */
	protected JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(siteModel);
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					
					changeSelectionOfQueues(((String)(siteModel.getSelectedItem())));
				}
			});
		}
		return siteComboBox;
	}
	
	private void changeSelectionOfQueues(String site) {

		queueModel.removeAllElements();
		for ( SubmissionLocation subLoc : infoObject.getCurrentSubmissionLocationsForSite(site) ) {
				queueModel.addElement(subLoc);
		}
	}
	
	
	/**
	 * @return
	 */
	protected JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
		}
		return queueComboBox;
	}
	
	private void switchMode(String mode) {
		
		if ( ANY_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.ANY_VERSION_MODE);
		} else if ( DEFAULT_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.DEFAULT_VERSION_MODE);
		} else if ( EXACT_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.EXACT_VERSION_MODE);
		}
		
		myLogger.error("Mode not supported: "+mode);
	}
	
	private void switchMode(int mode) {
		try {
			
			switch (mode) {
			case ApplicationInfoObject.ANY_VERSION_MODE:
				switchToAnyVersionMode(); break;
			case ApplicationInfoObject.DEFAULT_VERSION_MODE:
				switchToDefaultVersionMode(); break;
			case ApplicationInfoObject.EXACT_VERSION_MODE:
				switchToExactVersionMode(); break;
				default:
					myLogger.error("Can't switch to mode: "+mode+". Not supported.");
			}
			
			fillSiteCombobox();
				
		} catch (ModeNotSupportedException e) {
			myLogger.error("Can't switch to mode: "+mode+". Not supported.");
		}
	}
	
	private void fillSiteCombobox() {
		
		if ( infoObject != null && infoObject.getCurrentSubmissionLocations() != null ) {
			siteModel.removeAllElements();
			for ( String site : infoObject.getCurrentSites() ) {
			
				siteModel.addElement(site);
			
			}
		}
	}
	
	private void switchToAnyVersionMode() throws ModeNotSupportedException {
		
		siteModel.removeAllElements();
		queueModel.removeAllElements();
		infoObject.setMode(ApplicationInfoObject.ANY_VERSION_MODE, null);
		

	}
	
	private void switchToDefaultVersionMode() throws ModeNotSupportedException {

		siteModel.removeAllElements();
		queueModel.removeAllElements();
		infoObject.setMode(ApplicationInfoObject.DEFAULT_VERSION_MODE, null);
	}
	
	private void switchToExactVersionMode() throws ModeNotSupportedException {

		siteModel.removeAllElements();
		queueModel.removeAllElements();
		infoObject.setMode(ApplicationInfoObject.EXACT_VERSION_MODE, ((String)(versionModel.getSelectedItem())));

	}
	
	

	public void actionPerformed(ActionEvent e) {

		switchMode(e.getActionCommand());
		
	}
	
	

}
