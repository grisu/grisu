package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.historyRepeater.HistoryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Cpus extends AbstractWidget {

	private static final String[] DEFAULT_CPUS = new String[] { "1", "2", "3",
			"4", "5", "6", "7", "8", "12", "16", "24", "32", "64" };
	private final DefaultComboBoxModel cpuModel = new DefaultComboBoxModel(
			DEFAULT_CPUS);

	private JComboBox comboBox;
	private ServiceInterface si;
	private HistoryManager hm;
	private final String historyKey = null;

	/**
	 * Create the panel.
	 */
	public Cpus() {
		super();
		setBorder(new TitledBorder(null, "Cpus", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getComboBox(), "2, 2, fill, default");

	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(cpuModel);
		}
		return comboBox;
	}

	public Integer getCpus() {
		String integerString = (String) getComboBox().getSelectedItem();
		try {
			Integer result = Integer.parseInt(integerString);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private void setCpus(Integer cpus) {
		if (cpuModel.getIndexOf(cpus.toString()) < 0) {
			cpuModel.addElement(cpus.toString());
		}
		cpuModel.setSelectedItem(cpus.toString());
	}

	@Override
	public void setValue(String value) {

		try {
			Integer temp = Integer.parseInt(value);
			setCpus(temp);
		} catch (Exception e) {
			myLogger.debug("Can't set cpus: " + e.getLocalizedMessage());
		}

	}

	@Override
	public String getValue() {
		return getCpus().toString();
	}

	@Override
	public void lockIUI(boolean lock) {

		getComboBox().setEnabled(!lock);

	}
}
