package org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Jobname extends AbstractInputPanel {
	private JTextField jobnameTextField;

	public Jobname(PanelConfig config) {
		super(config);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getJobnameTextField(), "2, 2, fill, default");
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {

		Map<String, String> defaultProperties = new HashMap<String, String>();

		defaultProperties.put(TITLE, "Jobname");
		defaultProperties.put(DEFAULT_VALUE, "gridJob");

		return defaultProperties;
	}

	private JTextField getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new JTextField();
			jobnameTextField.setColumns(10);
			jobnameTextField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					setValue("jobname", jobnameTextField.getText());
				}

			});
		}
		return jobnameTextField;
	}

	@Override
	protected String getValueAsString() {
		return getJobnameTextField().getText();
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {

		if ( "jobname".equals(e.getPropertyName()) ) {
			String newJobname = (String)e.getNewValue();
			getJobnameTextField().setText(newJobname);
		}

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) {

		String defaultValue = panelProperties.get(DEFAULT_VALUE);
		if ( StringUtils.isNotBlank(defaultValue) ) {
			setValue("jobname", defaultValue);
		}

	}
}
