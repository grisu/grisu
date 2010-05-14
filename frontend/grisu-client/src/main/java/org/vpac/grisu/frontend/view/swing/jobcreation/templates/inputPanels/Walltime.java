package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.exceptions.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Walltime extends AbstractInputPanel {

	public static int convertHumanReadableStringIntoSeconds(
			String[] humanReadable) {

		int amount = -1;
		try {
			amount = Integer.parseInt(humanReadable[0]);
		} catch (Exception e) {
			throw new RuntimeException("Could not parse string.", e);
		}
		String unit = humanReadable[1];

		if ("minutes".equals(unit)) {
			return amount * 60;
		} else if ("hours".equals(unit)) {
			return amount * 3600;
		} else if ("days".equals(unit)) {
			return amount * 3600 * 24;
		} else {
			// throw new RuntimeException(unit+" not a supported unit name.");
			return amount * 60; // default
		}

	}

	public static String[] convertSecondsInHumanReadableString(
			int walltimeInSeconds) {

		int days = walltimeInSeconds / (3600 * 24);
		int hours = (walltimeInSeconds - (days * 3600 * 24)) / 3600;
		int minutes = (walltimeInSeconds - ((days * 3600 * 24) + (hours * 3600))) / 60;

		if ((days > 0) && (hours == 0) && (minutes == 0)) {
			return new String[] { new Integer(days).toString(), "days" };
		} else if ((days > 0) && (hours == 0)) {
			// fuck the minutes
			return new String[] { new Integer(days).toString(), "days" };
		} else if ((days > 0) && (hours > 0)) {
			return new String[] { new Integer(days * 24 + hours).toString(),
					"hours" };
		} else if ((days == 0) && (hours > 0) && (minutes == 0)) {
			return new String[] { new Integer(hours).toString(), "hours" };
		} else if ((days == 0) && (hours > 0) && (minutes > 0)) {
			if (hours > 6) {
				// fuck the minutes
				return new String[] { new Integer(hours).toString(), "hours" };
			} else {
				return new String[] {
						new Integer(hours * 60 + minutes).toString(), "minutes" };
			}
		} else {
			return new String[] { new Integer(minutes).toString(), "minutes" };
		}

	}

	private JComboBox amountComboBox;

	private JComboBox unitComboBox;
	private final DefaultComboBoxModel amountModel = new DefaultComboBoxModel();

	private final DefaultComboBoxModel unitModel = new DefaultComboBoxModel();

	public Walltime(String name, PanelConfig config) throws TemplateException {

		super(name, config);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("27dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(42dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getAmountComboBox(), "2, 2, fill, fill");
		add(getUnitComboBox(), "4, 2, fill, fill");
	}

	private JComboBox getAmountComboBox() {
		if (amountComboBox == null) {
			amountComboBox = new JComboBox();
			amountComboBox.setEditable(true);
			amountComboBox.setModel(amountModel);
			// amountComboBox.setSelectedIndex(0);
			amountComboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					String amount = (String) (getAmountComboBox()
							.getSelectedItem());
					String unit = (String) (getUnitComboBox().getSelectedItem());

					if (StringUtils.isBlank(amount)
							|| StringUtils.isBlank(unit)) {
						return;
					}

					int walltimeInSeconds = -1;
					try {
						walltimeInSeconds = convertHumanReadableStringIntoSeconds(new String[] {
								amount, unit });
					} catch (Exception e1) {
						myLogger.debug("Can't parse " + amount + ",  " + unit
								+ ": " + e1.getLocalizedMessage());
						return;
					}

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
		defaultProperties.put("defaultAmountList",
				"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,30,45");
		defaultProperties.put("defaultAmount", "1");
		defaultProperties.put("defaultUnitList", "minutes,hours,days,weeks");
		defaultProperties.put("defaultUnit", "hours");

		return defaultProperties;
	}

	@Override
	public JComboBox getJComboBox() {
		return null;
	}

	@Override
	public JTextComponent getTextComponent() {
		return null;
	}

	private JComboBox getUnitComboBox() {
		if (unitComboBox == null) {
			unitComboBox = new JComboBox();
			unitComboBox.setModel(unitModel);
			// unitComboBox.setSelectedIndex(1);
			unitComboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					int walltimeInSeconds = convertHumanReadableStringIntoSeconds(new String[] {
							(String) (getAmountComboBox().getSelectedItem()),
							(String) (getUnitComboBox().getSelectedItem()) });
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
		String amount = (String) getAmountComboBox().getSelectedItem();
		String unit = (String) getUnitComboBox().getSelectedItem();
		try {
			Integer secs = convertHumanReadableStringIntoSeconds(new String[] {
					amount, unit });
			return secs.toString();
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {

		if ("walltimeInSeconds".equals(e.getPropertyName())) {

			String[] humanReadable = convertSecondsInHumanReadableString((Integer) (e
					.getNewValue()));
			amountModel.setSelectedItem(humanReadable[0]);
			unitModel.setSelectedItem(humanReadable[1]);
		}

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) {

		String[] amounts = panelProperties.get("defaultAmountList").split(",");
		amountModel.removeAllElements();
		for (String amount : amounts) {
			try {
				Integer a = Integer.parseInt(amount);
				amountModel.addElement(amount);
			} catch (Exception e) {
				myLogger.error("Can't add amount " + amount
						+ " to WalltimePanel: " + e.getLocalizedMessage());
			}
		}

		unitModel.removeAllElements();
		String[] units = panelProperties.get("defaultUnitList").split(",");
		for (String unit : units) {
			if ("minutes,hours,days,weeks".indexOf(unit) >= 0) {
				unitModel.addElement(unit);
			} else {
				myLogger.error("Can't add unit " + unit
						+ " to WalltimePanel. Not a valid unitname.");
			}
		}

	}

	@Override
	void setInitialValue() throws TemplateException {

		String defaultAmount = null;
		String defaultUnit = null;
		if (useHistory()) {
			String defValue = getDefaultValue();

			try {
				String[] humanreadable = convertSecondsInHumanReadableString(Integer
						.parseInt(defValue));
				if (humanreadable != null && humanreadable.length == 2) {
					defaultAmount = humanreadable[0];
					defaultUnit = humanreadable[1];
				}
			} catch (Exception e) {
				myLogger.debug("Can't parse history value for walltime: "
						+ e.getLocalizedMessage());
			}

		}

		if (StringUtils.isBlank(defaultAmount)) {
			defaultAmount = getPanelProperty("defaultAmount");
		}
		if (StringUtils.isNotBlank(defaultAmount)) {
			try {
				Integer a = Integer.parseInt(defaultAmount);
				amountModel.setSelectedItem(defaultAmount);
			} catch (Exception e) {
				myLogger.error("Can't set amount " + defaultAmount
						+ " as default to WalltimePanel: "
						+ e.getLocalizedMessage());
			}
		}

		if (StringUtils.isBlank(defaultUnit)) {
			defaultUnit = getPanelProperty("defaultUnit");
		}

		if (StringUtils.isNotBlank(defaultUnit)
				&& ("minutes,hours,days,weeks".indexOf(defaultUnit) >= 0)) {
			unitModel.setSelectedItem(defaultUnit);
		} else {
			myLogger.error("Can't set unit " + defaultUnit
					+ " as default to WalltimePanel. Not a valid unitname.");
		}

	}

	@Override
	protected void templateRefresh(JobSubmissionObjectImpl jobObject) {

		if (useHistory()) {
			addValueToHistory();
		}
	}
}