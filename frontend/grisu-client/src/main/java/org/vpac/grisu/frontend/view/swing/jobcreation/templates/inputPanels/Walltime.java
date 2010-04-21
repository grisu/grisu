package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class Walltime extends AbstractInputPanel {

	public static int convertHumanReadableStringIntoSeconds(String[] humanReadable) {


		int amount = -1;
		try {
			amount = Integer.parseInt(humanReadable[0]);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse string.", e);
		}
		String unit = humanReadable[1];

		if ( "minutes".equals(unit) ) {
			return amount * 60;
		} else if ( "hours".equals(unit) ) {
			return amount * 3600;
		} else if ("days".equals(unit) ) {
			return amount * 3600 * 24;
		} else {
			//			throw new RuntimeException(unit+" not a supported unit name.");
		}

	}

	public static String[] convertSecondsInHumanReadableString(int walltimeInSeconds) {

		int days = walltimeInSeconds / (3600 * 24);
		int hours = (walltimeInSeconds - (days * 3600 * 24)) / 3600;
		int minutes = (walltimeInSeconds - ((days * 3600 * 24) + (hours * 3600))) / 60;

		if ( (days > 0) && (hours == 0) && (minutes == 0)) {
			return new String[]{new Integer(days).toString(), "days"};
		} else if ( (days > 0) && (hours == 0) ) {
			// fuck the minutes
			return  new String[]{new Integer(days).toString(), "days"};
		} else if ( (days > 0) && (hours > 0) ) {
			return new String[]{new Integer(days*24+hours).toString(), "hours"};
		} else if ( (days == 0) && (hours > 0) && (minutes == 0) ) {
			return new String[]{new Integer(hours).toString(), "hours"};
		} else if ( (days == 0) && (hours > 0) && (minutes > 0) ) {
			if ( hours > 6 ) {
				// fuck the minutes
				return new String[]{new Integer(hours).toString(), "hours"};
			} else {
				return new String[]{new Integer(hours*60+minutes).toString(), "minutes"};
			}
		} else {
			return new String[]{new Integer(minutes).toString(), "minutes"};
		}

	}


	private JComboBox amountComboBox;

	private JComboBox unitComboBox;
	private final DefaultComboBoxModel amountModel = new DefaultComboBoxModel();

	private final DefaultComboBoxModel unitModel = new DefaultComboBoxModel();

	public Walltime(PanelConfig config) throws TemplateException {
		super(config);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getAmountComboBox(), "2, 2, fill, bottom");
		add(getUnitComboBox(), "4, 2, fill, bottom");
	}

	private JComboBox getAmountComboBox() {
		if (amountComboBox == null) {
			amountComboBox = new JComboBox();
			amountComboBox.setEditable(true);
			amountComboBox.setModel(amountModel);
			//			amountComboBox.setSelectedIndex(0);
			amountComboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					int walltimeInSeconds = convertHumanReadableStringIntoSeconds(
							new String[]{
									(String)(getAmountComboBox().getSelectedItem()),
									(String)(getUnitComboBox().getSelectedItem())});
					try {
						setValue("walltimeInSeconds", walltimeInSeconds);
					} catch (TemplateException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		return amountComboBox;
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {

		Map<String, String> defaultProperties = new HashMap<String, String>();
		defaultProperties.put(TITLE, "Walltime");
		defaultProperties.put("defaultAmountList", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,30,45");
		defaultProperties.put("defaultAmount", "1");
		defaultProperties.put("defaultUnitList", "minutes,hours,days,weeks");
		defaultProperties.put("defaultUnit", "hours");

		return defaultProperties;
	}

	private JComboBox getUnitComboBox() {
		if (unitComboBox == null) {
			unitComboBox = new JComboBox();
			unitComboBox.setModel(unitModel);
			//			unitComboBox.setSelectedIndex(1);
			unitComboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					int walltimeInSeconds = convertHumanReadableStringIntoSeconds(
							new String[]{
									(String)(getAmountComboBox().getSelectedItem()),
									(String)(getUnitComboBox().getSelectedItem())});
					try {
						setValue("walltimeInSeconds", walltimeInSeconds);
					} catch (TemplateException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return unitComboBox;
	}

	@Override
	protected String getValueAsString() {
		throw new RuntimeException("Not implemented yet. Should not be needed.");
	}
	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {

		if ( "walltimeInSeconds".equals(e.getPropertyName()) ) {

			String[] humanReadable = convertSecondsInHumanReadableString((Integer)(e.getNewValue()));
			amountModel.setSelectedItem(humanReadable[0]);
			unitModel.setSelectedItem(humanReadable[1]);
		}

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) {

		String[] amounts = panelProperties.get("defaultAmountList").split(",");
		for ( String amount : amounts ) {
			try {
				Integer a = Integer.parseInt(amount);
				amountModel.addElement(amount);
			} catch (Exception e) {
				myLogger.error("Can't add amount "+amount+" to WalltimePanel: "+e.getLocalizedMessage());
			}
		}
		String defaultAmount = panelProperties.get("defaultAmount");
		if ( StringUtils.isNotBlank(defaultAmount) ) {
			try {
				Integer a = Integer.parseInt(defaultAmount);
				amountModel.setSelectedItem(defaultAmount);
			} catch (Exception e) {
				myLogger.error("Can't set amount "+defaultAmount+" as default to WalltimePanel: "+e.getLocalizedMessage());
			}
		}

		String[] units = panelProperties.get("defaultUnitList").split(",");
		for ( String unit : units ) {
			if ( "minutes,hours,days,weeks".indexOf(unit) >= 0 ) {
				unitModel.addElement(unit);
			} else {
				myLogger.error("Can't add unit "+unit+" to WalltimePanel. Not a valid unitname.");
			}
		}
		String defaultUnit = panelProperties.get("defaultUnit");
		if ( StringUtils.isNotBlank(defaultUnit) && ("minutes,hours,days,weeks".indexOf(defaultUnit) >= 0) ) {
			unitModel.setSelectedItem(defaultUnit);
		} else {
			myLogger.error("Can't set unit "+defaultUnit+" as default to WalltimePanel. Not a valid unitname.");
		}

	}
}
