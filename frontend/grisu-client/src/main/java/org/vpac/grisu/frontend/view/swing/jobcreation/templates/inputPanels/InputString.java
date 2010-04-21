package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class InputString extends AbstractInputPanel {
	private JTextField textField;

	public InputString(PanelConfig config) throws TemplateException {
		super(config);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getTextField(), "2, 2, fill, default");
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {

		Map<String, String> defaultProperties = new HashMap<String, String>();

		return defaultProperties;
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}

	@Override
	protected String getValueAsString() {
		return getTextField().getText();
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) throws TemplateException {

		if ( StringUtils.isBlank(bean) ) {
			return;
		}

		String defaultValue = panelProperties.get(DEFAULT_VALUE);
		if ( StringUtils.isNotBlank(defaultValue) ) {
			setValue(bean, defaultValue);
		}

	}
}
