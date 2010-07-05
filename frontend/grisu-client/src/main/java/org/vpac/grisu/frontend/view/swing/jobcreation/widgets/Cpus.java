package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JComboBox;

public class Cpus extends JPanel {
	
	private static final String[] DEFAULT_CPUS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "12", "16", "24", "32", "64"};
	private DefaultComboBoxModel cpuModel = new DefaultComboBoxModel(DEFAULT_CPUS);
	
	private JComboBox comboBox;

	/**
	 * Create the panel.
	 */
	public Cpus() {
		setBorder(new TitledBorder(null, "Cpus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getComboBox(), "2, 2, fill, default");

	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(cpuModel);
		}
		return comboBox;
	}
	
	public int getCpus() {
		String integerString = (String)getComboBox().getSelectedItem();
		try {
			Integer result = Integer.parseInt(integerString);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
