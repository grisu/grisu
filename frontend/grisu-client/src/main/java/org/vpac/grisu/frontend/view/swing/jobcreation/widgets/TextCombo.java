package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.historyRepeater.HistoryManager;

public class TextCombo extends JPanel {
	private JComboBox comboBox;
	
	private DefaultComboBoxModel textModel = new DefaultComboBoxModel();
	
	private ServiceInterface si;
	private HistoryManager hm;
	private String historyKey;

	/**
	 * Create the panel.
	 */
	public TextCombo() {
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
	
	public void setTitle(String title) {
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(textModel);
			comboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			comboBox.setEditable(true);
		}
		return comboBox;
	}
	
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.hm = GrisuRegistryManager.getDefault(si).getHistoryManager();
	}
	
	public void setHistoryKey(String historyKey) {
		this.historyKey = historyKey;
		for ( String entry : hm.getEntries(this.historyKey) ) {
			textModel.addElement(entry);
		}
	}
	
	public String getText() {
		return (String)getComboBox().getSelectedItem();
	}
	
	public void setText(String text) {
		getComboBox().addItem(text);
		getComboBox().setSelectedItem(text);
	}
	
	public void saveItemToHistory() {
		if ( StringUtils.isNotBlank(getText()) ) {
			hm.addHistoryEntry(this.historyKey, getText());
		}
	}
}

