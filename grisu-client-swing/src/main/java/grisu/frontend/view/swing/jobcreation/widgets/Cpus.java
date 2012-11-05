package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.control.ServiceInterface;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.vpac.historyRepeater.HistoryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Cpus extends AbstractWidget {

	private static final String[] DEFAULT_CPUS = new String[] { "1", "2", "3",
		"4", "5", "6", "7", "8", "12", "16", "24", "32", "64", "128" };
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
	
	public void setEditableComboBox(boolean editable) {
		getComboBox().setEditable(editable);
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(cpuModel);
			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent itemevent) {
					if (itemevent.getStateChange() == ItemEvent.SELECTED) {
						getPropertyChangeSupport().firePropertyChange("cpus",
								-1, getValue());

					}
				}
			});
		}
		return comboBox;
	}

	public Integer getCpus() {
		final String integerString = (String) getComboBox().getSelectedItem();
		try {
			final Integer result = Integer.parseInt(integerString);
			return result;
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return -1;
		}
	}

	@Override
	public String getValue() {
		return getCpus().toString();
	}

	@Override
	public void lockUI(boolean lock) {

		getComboBox().setEnabled(!lock);

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
			final Integer temp = Integer.parseInt(value);
			setCpus(temp);
		} catch (final Exception e) {
			myLogger.debug("Can't set cpus: " + e.getLocalizedMessage());
		}

	}
}
