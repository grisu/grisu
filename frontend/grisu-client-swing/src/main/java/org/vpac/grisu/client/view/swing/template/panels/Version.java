package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.js.model.InformationObject;

public class Version extends JPanel implements TemplateNodePanel,
		ActionListener {

	static final Logger myLogger = Logger.getLogger(Version.class.getName());

	public static final String ANY_MODE_TEMPLATETAG_KEY = "useAny";
	public static final String DEFAULT_MODE_TEMPLATETAG_KEY = "useDefault";
	public static final String EXACT_MODE_TEMPLATETAG_KEY = "useExact";

	public static final String ANY_MODE_STRING = "Any";
	public static final String DEFAULT_MODE_STRING = "Default";
	public static final String EXACT_MODE_STRING = "Exact";

	public static final String STARTUP_MODE_KEY = "startMode";

	private JComboBox versionComboBox;
	private DefaultComboBoxModel versionModel = new DefaultComboBoxModel();
	private JRadioButton anyRadioButton;
	private JRadioButton defaultRadioButton;
	private JRadioButton exactRadioButton;

	private TemplateNode templateNode;
	private InformationObject infoObject = null;
	private String applicationName = null;
	private boolean useAny = true;
	private boolean useDefault = true;
	private String defaultVersion;
	private boolean useExact = true;

	private String startMode = ANY_MODE_STRING;

	private ButtonGroup modeGroup = new ButtonGroup();

	/**
	 * Create the panel
	 */
	public Version() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new TitledBorder(null, "Version",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		//
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		
		this.applicationName = this.templateNode.getTemplate().getApplicationName();
		
		this.useAny = node.getOtherProperties().containsKey(
				ANY_MODE_TEMPLATETAG_KEY);
		this.useDefault = node.getOtherProperties().containsKey(
				DEFAULT_MODE_TEMPLATETAG_KEY);
		this.useExact = node.getOtherProperties().containsKey(
				EXACT_MODE_TEMPLATETAG_KEY);

		if (node.getOtherProperties().containsKey(STARTUP_MODE_KEY)) {
			startMode = node.getOtherProperty(STARTUP_MODE_KEY);
			if (ANY_MODE_STRING.equals(startMode)) {
				useAny = true;
			} else if (EXACT_MODE_STRING.equals(startMode)) {
				useExact = true;
			} else if (DEFAULT_MODE_STRING.endsWith(startMode)) {
				defaultVersion = node.getOtherProperty(DEFAULT_MODE_STRING);
				if (defaultVersion == null || "".equals(defaultVersion)) {
					useDefault = false;
					myLogger
							.warn("Not using default mode because no default version value is specified in template.");
				} else {
					useDefault = true;
				}
			}
		} else {
			if (useAny) {
				startMode = ANY_MODE_STRING;
			} else if (useExact) {
				startMode = EXACT_MODE_STRING;
			} else if (useDefault) {
				startMode = DEFAULT_MODE_STRING;
			} else {
				useAny = true;
				startMode = ANY_MODE_STRING;
			}
		}

		if (useAny) {
			add(getAnyRadioButton());
		}
		if (useDefault) {
			add(getDefaultRadioButton());
		}
		if (useExact) {
			add(getExactRadioButton());
		}

		// add combobox as last item
		add(getVersionComboBox());

		infoObject = GrisuRegistry.getDefault().getInformationObject(applicationName);
		
//		for ( String version : infoObject.getAvailableVersions(subLoc) ) {
//			versionModel.addElement(anObject)
//		}
		
	}

	private void switchMode(String mode) {

		if (ANY_MODE_STRING.equals(mode)) {
			switchMode(InformationObject.ANY_VERSION_MODE);
			return;
		} else if (DEFAULT_MODE_STRING.equals(mode)) {
			switchMode(InformationObject.DEFAULT_VERSION_MODE);
			return;
		} else if (EXACT_MODE_STRING.equals(mode)) {
			switchMode(InformationObject.EXACT_VERSION_MODE);
			return;
		}

		myLogger.error("Mode not supported: " + mode);
	}

	private void switchMode(int mode) {

		switch (mode) {
		case InformationObject.ANY_VERSION_MODE:
			switchToAnyVersionMode();
			break;
		case InformationObject.DEFAULT_VERSION_MODE:
			switchToDefaultVersionMode();
			break;
		case InformationObject.EXACT_VERSION_MODE:
			switchToExactVersionMode();
			break;
		default:
			myLogger
					.error("Can't switch to mode: " + mode + ". Not supported.");
		}

	}

	private void switchToExactVersionMode() {

		getVersionComboBox().setEnabled(true);

	}

	private void switchToDefaultVersionMode() {
		// TODO Auto-generated method stub

	}

	private void switchToAnyVersionMode() {
		// TODO Auto-generated method stub

	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		// TODO Auto-generated method stub

	}

	public String getExternalSetValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExternalSetValue(String value) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	protected JRadioButton getExactRadioButton() {
		if (exactRadioButton == null) {
			exactRadioButton = new JRadioButton();
			exactRadioButton.setText("Exact");
			modeGroup.add(exactRadioButton);
		}
		return exactRadioButton;
	}

	/**
	 * @return
	 */
	protected JRadioButton getDefaultRadioButton() {
		if (defaultRadioButton == null) {
			defaultRadioButton = new JRadioButton();
			defaultRadioButton.setText("Default");
			modeGroup.add(defaultRadioButton);
		}
		return defaultRadioButton;
	}

	/**
	 * @return
	 */
	protected JRadioButton getAnyRadioButton() {
		if (anyRadioButton == null) {
			anyRadioButton = new JRadioButton();
			anyRadioButton.setText("Any");
			modeGroup.add(anyRadioButton);
		}
		return anyRadioButton;
	}

	/**
	 * @return
	 */
	protected JComboBox getVersionComboBox() {
		if (versionComboBox == null) {
			versionComboBox = new JComboBox(versionModel);
			versionComboBox.setMaximumSize(new Dimension(300, 24));
			versionComboBox.setEditable(false);
		}
		return versionComboBox;
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	// event stuff
	// ========================================================

	private Vector<ValueListener> valueChangedListeners;

	private void fireVersionChanged(String newValue) {

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

	public void actionPerformed(ActionEvent e) {
		switchMode(e.getActionCommand());
	}

}
