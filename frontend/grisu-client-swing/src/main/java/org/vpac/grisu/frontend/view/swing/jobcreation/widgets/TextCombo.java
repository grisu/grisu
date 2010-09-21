package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TextCombo extends AbstractWidget {
	private JComboBox comboBox;

	private final DefaultComboBoxModel textModel = new DefaultComboBoxModel();

	/**
	 * Create the panel.
	 */
	public TextCombo() {
		super();
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
			comboBox = new JComboBox(textModel);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			comboBox.setEditable(true);
		}
		return comboBox;
	}

	public String getText() {
		return (String) getComboBox().getSelectedItem();
	}

	@Override
	public String getValue() {
		return getText();
	}

	@Override
	public void historyKeySet() {
		getHistoryManager().setMaxNumberOfEntries(getHistoryKey(), 8);
		for (final String entry : getHistoryManager().getEntries(
				getHistoryKey())) {
			if (textModel.getIndexOf(entry) < 0) {
				getComboBox().addItem(entry);
			}
		}
	}

	@Override
	public void lockIUI(boolean lock) {

		getComboBox().setEnabled(!lock);

	}

	public void setText(String text) {
		if (textModel.getIndexOf(text) < 0) {
			getComboBox().addItem(text);
		}
		getComboBox().setSelectedItem(text);
	}

	@Override
	public void setValue(String value) {
		setText(value);
	}

}
