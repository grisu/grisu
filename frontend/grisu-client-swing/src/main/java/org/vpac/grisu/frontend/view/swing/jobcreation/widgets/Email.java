package org.vpac.grisu.frontend.view.swing.jobcreation.widgets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Email extends AbstractWidget {
	private JCheckBox checkBox;
	private JCheckBox checkBox_1;
	private JLabel label;
	private JTextField textField;

	public static final String EMAIL_HISTORY_KEY_GENERIC = "send_user_email";

	public Email() {
		super();
		setBorder(new TitledBorder(null, "Send email when job is...",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(33dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getCheckBox(), "2, 2, 3, 1");
		add(getCheckBox_1(), "6, 2");
		add(getLabel(), "2, 4, right, default");
		add(getTextField(), "4, 4, 3, 1, fill, default");

		setHistoryKey(EMAIL_HISTORY_KEY_GENERIC);
	}

	@Override
	public String getValue() {
		String emailAddress = getEmailAddress();
		if (StringUtils.isBlank(emailAddress)) {
			emailAddress = "null";
		}
		String value = getCheckBox().isSelected() + ","
				+ getCheckBox_1().isSelected() + "," + emailAddress;
		return value;
	}

	@Override
	public void setValue(String value) {

		if (StringUtils.isBlank(value)) {
			return;
		}

		try {
			String[] string = value.split(",");
			boolean onStart = Boolean.parseBoolean(string[0]);
			boolean onFinish = Boolean.parseBoolean(string[1]);
			String emailAddress = string[2];
			if (emailAddress == null || "null".equals(emailAddress)) {
				emailAddress = "";
			}
			getCheckBox().setSelected(onStart);
			getCheckBox_1().setSelected(onFinish);
			getTextField().setText(emailAddress);

		} catch (Exception e) {
			return;
		}
	}

	private JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox("...started");
		}
		return checkBox;
	}

	private JCheckBox getCheckBox_1() {
		if (checkBox_1 == null) {
			checkBox_1 = new JCheckBox("...finished");
		}
		return checkBox_1;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("Email:");
		}
		return label;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}

	public boolean sendEmailWhenJobFinished() {
		return getCheckBox_1().isSelected();
	}

	public boolean sendEmailWhenJobIsStarted() {
		return getCheckBox().isSelected();
	}

	public String getEmailAddress() {
		return getTextField().getText();
	}
}
