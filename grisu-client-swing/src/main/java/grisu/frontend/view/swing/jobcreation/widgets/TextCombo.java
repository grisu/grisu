package grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
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
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getComboBox(), "2, 2, fill, default");

	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(textModel);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			comboBox.setEditable(true);
			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent itemevent) {
					if (itemevent.getStateChange() == ItemEvent.SELECTED) {
						getPropertyChangeSupport().firePropertyChange(
								getTitle(), -1, getValue());

					}
				}
			});

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
	public void lockUI(boolean lock) {

		getComboBox().setEnabled(!lock);

	}

	public void setSelectionValues(String[] values) {
		textModel.removeAllElements();
		for (String v : values) {
			textModel.addElement(v);
		}
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
