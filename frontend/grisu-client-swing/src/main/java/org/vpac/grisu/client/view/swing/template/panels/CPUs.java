package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CPUs extends JPanel implements TemplateNodePanel {
	
	static final Logger myLogger = Logger.getLogger(CPUs.class.getName());

	private JLabel errorLabel;
	private TemplateNode templateNode = null;
	
	public static String[] DEFAULT_CPUS = new String[]{"1", "2", "4", "8", "16", "32"};
	
	private JLabel label;
	private JComboBox comboBox;
	private JCheckBox checkBox;
	
	private DefaultComboBoxModel cpuComboBoxModel = new DefaultComboBoxModel();
	/**
	 * Create the panel
	 */
	public CPUs() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBorder(new TitledBorder(null, "CPUs", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		add(getCheckBox(), new CellConstraints(2, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getComboBox(), new CellConstraints(2, 7, CellConstraints.FILL, CellConstraints.BOTTOM));
		add(getLabel(), new CellConstraints(2, 5, CellConstraints.LEFT, CellConstraints.BOTTOM));
		add(getErrorLabel(), new CellConstraints(2, 3));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

//	public void  setTemplateNodeValue() {
//		try {
//			if ( getCheckBox().isSelected() )
//				this.templateNode.setValue((String)getComboBox().getSelectedItem());
//			else
//				this.templateNode.setValue("1");
//		} catch (TemplateValidateException e) {
//			errorLabel.setText(e.getLocalizedMessage());
//			errorLabel.setToolTipText(e.getLocalizedMessage());
//			errorLabel.setVisible(true);
//		}
//	}
	/**
	 * @return
	 */
	protected JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox();
			checkBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					getComboBox().setEnabled(getCheckBox().isSelected());
					fireSitePanelEvent((String)cpuComboBoxModel.getSelectedItem());
				}
			});
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setHorizontalTextPosition(SwingConstants.LEADING);
			checkBox.setText("MPI job");
		}
		return checkBox;
	}
	
	public boolean isMultiCPUchecked() {
		return getCheckBox().isSelected();
	}
	
	public int getSelectedNoOfCPUs() {
		return Integer.parseInt((String)cpuComboBoxModel.getSelectedItem());
	}
	
	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(cpuComboBoxModel);
			comboBox.setEditable(true);
			comboBox.setEnabled(false);
		}
		return comboBox;
	}
	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("No. of cpus");
		}
		return label;
	}

	public void setTemplateNode(TemplateNode node) throws TemplateNodePanelException {
		
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		
		node.addTemplateNodeListener(this);
		
		String[] defaultValues = node.getPrefills();
		
		if ( defaultValues == null || defaultValues.length == 0 ) {
			defaultValues = DEFAULT_CPUS;
		}
		
		for ( String value : defaultValues ) {
			cpuComboBoxModel.addElement(value);
		}
		
		String defaultValue = node.getDefaultValue();
		if ( defaultValue != null && ! "".equals(defaultValue) ) {
			if ( "1".equals(defaultValue) ) {
				getCheckBox().setSelected(false);
				getComboBox().setEnabled(false);
			} else {
				getCheckBox().setSelected(true);
				getComboBox().setEnabled(true);
			}
			cpuComboBoxModel.setSelectedItem(defaultValue);
		}
	}
	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setVisible(false);
			errorLabel.setForeground(Color.RED);
		}
		return errorLabel;
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		
		if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID ) {
			String message = event.getMessage();
			if ( message == null ) 
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;
			
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		} else if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID ) {
			errorLabel.setVisible(false);
		} 
		
	}

	public String getExternalSetValue() {
		
		if ( getCheckBox().isSelected() )
			return (String)getComboBox().getSelectedItem();
		else
			return "1";

	}
	
	public void setExternalSetValue(String value) {
		if ( value != null ) {
			getCheckBox().setSelected(true);
			getComboBox().setSelectedItem(value);
		} else { 
			getCheckBox().setSelected(false);
		}
	}

	public void reset() {

		// do nothing
		
	}

	
	// event stuff
	// ========================================================
	
	private Vector<ValueListener> valueChangedListeners;

	private void fireSitePanelEvent(String newValue) {
		
		myLogger.debug("Fire value changed event: new value: "+newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners.clone();
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


	
}
