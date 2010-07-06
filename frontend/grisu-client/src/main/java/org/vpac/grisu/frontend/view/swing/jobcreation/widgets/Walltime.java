package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.vpac.grisu.frontend.view.swing.utils.WalltimeUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Walltime extends AbstractWidget {

	private final static String[] units = new String[] { "minutes", "hours",
			"days", "weeks" };
	private final static String[] amounts = new String[] { "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "30",
			"45" };

	private final DefaultComboBoxModel unitModel = new DefaultComboBoxModel(
			units);
	private final DefaultComboBoxModel amountsModel = new DefaultComboBoxModel(
			amounts);

	private JComboBox amountComboBox;
	private JComboBox unitsComboBox;

	/**
	 * Create the panel.
	 */
	public Walltime() {
		super();
		setBorder(new TitledBorder(null, "Walltime", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(58dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(49dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getAmountComboBox(), "2, 2, fill, default");
		add(getUnitsComboBox(), "4, 2, fill, default");

	}

	private JComboBox getAmountComboBox() {
		if (amountComboBox == null) {
			amountComboBox = new JComboBox(amountsModel);
			amountComboBox.setEditable(true);
		}
		return amountComboBox;
	}

	private JComboBox getUnitsComboBox() {
		if (unitsComboBox == null) {
			unitsComboBox = new JComboBox(unitModel);
			unitsComboBox.setEditable(false);
		}
		return unitsComboBox;
	}

	public void setWalltimeInSeconds(int seconds) {

		String[] humanReadable = WalltimeUtils
				.convertSecondsInHumanReadableString(seconds);
		amountsModel.setSelectedItem(humanReadable[0]);
		unitModel.setSelectedItem(humanReadable[1]);

	}

	public Integer getWalltimeInSeconds() {
		Integer secs = null;
		try {
			String amount = (String) getAmountComboBox().getSelectedItem();
			String unit = (String) getUnitsComboBox().getSelectedItem();
			secs = WalltimeUtils
					.convertHumanReadableStringIntoSeconds(new String[] {
							amount, unit });
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return secs;
	}

	@Override
	public void setValue(String value) {

		try {
			int temp = Integer.parseInt(value);
			setWalltimeInSeconds(temp);
		} catch (Exception e) {
			myLogger.debug("Error setting value for walltime widget: "
					+ e.getLocalizedMessage());
			return;
		}

	}

	@Override
	public String getValue() {
		return getWalltimeInSeconds().toString();
	}

	public void lockIUI(boolean lock) {

		getUnitsComboBox().setEnabled(!lock);
		getAmountComboBox().setEnabled(!lock);

	}

}
