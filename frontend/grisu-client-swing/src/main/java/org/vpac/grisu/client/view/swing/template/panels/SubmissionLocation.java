package org.vpac.grisu.client.view.swing.template.panels;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.model.UserApplicationInformation;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SubmissionLocation extends JPanel implements TemplateNodePanel, ValueListener {
	
	private JComboBox comboBox_1;
	private JComboBox comboBox;
	private JLabel label_1;
	private JLabel label;
	static final Logger myLogger = Logger.getLogger(SubmissionLocation.class.getName());
	
	private TemplateNode templateNode;
	private String applicationName;
	
	private UserApplicationInformation infoObject = null;
	private TemplateNodePanel versionPanel = null;

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
		add(getComboBox(), new CellConstraints(4, 2));
		add(getComboBox_1(), new CellConstraints(4, 4));
		//
	}

	public JPanel getTemplateNodePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		
		this.applicationName = this.templateNode.getTemplate().getApplicationName();
		this.infoObject = GrisuRegistry.getDefault().getUserApplicationInformation(applicationName);
		
		// this might be slightly dodgy. But it should always work if a Version template tag is present.
		versionPanel = (Version)(this.templateNode.getTemplate().getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).getTemplateNodeValueSetter());
		versionPanel.addValueListener(this);
		
	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {
		// version changed...
		System.out.println("Hi. Here's submissionlocationpanel. Version changed to: "+newValue);
		

	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		
	}

	public String getExternalSetValue() {
		// TODO Auto-generated method stub
		return null;
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
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
		}
		return comboBox;
	}
	/**
	 * @return
	 */
	protected JComboBox getComboBox_1() {
		if (comboBox_1 == null) {
			comboBox_1 = new JComboBox();
		}
		return comboBox_1;
	}

}
