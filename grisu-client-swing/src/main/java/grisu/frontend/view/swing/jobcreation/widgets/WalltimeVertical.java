package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.jcommons.utils.WalltimeUtils;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class WalltimeVertical extends AbstractWidget {

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
	public WalltimeVertical() {
		super();
		setBorder(new TitledBorder(null, "Walltime", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(45dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getAmountComboBox(), "2, 2, fill, default");
		add(getUnitsComboBox(), "2, 4, fill, default");

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

	@Override
	public String getValue() {
		return getWalltimeInSeconds().toString();
	}

	public Integer getWalltimeInSeconds() {
		Integer secs = null;
		try {
			final String amount = (String) getAmountComboBox()
					.getSelectedItem();
			final String unit = (String) getUnitsComboBox().getSelectedItem();
			secs = WalltimeUtils
					.convertHumanReadableStringIntoSeconds(new String[] {
							amount, unit });
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return -1;
		}
		return secs;
	}

	@Override
	public void lockUI(boolean lock) {

		getUnitsComboBox().setEnabled(!lock);
		getAmountComboBox().setEnabled(!lock);

	}

	@Override
	public void setValue(String value) {

		try {
			final int temp = Integer.parseInt(value);
			setWalltimeInSeconds(temp);
		} catch (final Exception e) {
			myLogger.debug("Error setting value for walltime widget: "
					+ e.getLocalizedMessage());
			return;
		}

	}

	public void setWalltimeInSeconds(int seconds) {

		final String[] humanReadable = WalltimeUtils
				.convertSecondsInHumanReadableString(seconds);
		amountsModel.setSelectedItem(humanReadable[0]);
		unitModel.setSelectedItem(humanReadable[1]);

	}

}
