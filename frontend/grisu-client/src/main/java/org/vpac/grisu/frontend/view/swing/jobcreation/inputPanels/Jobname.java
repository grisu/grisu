package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Jobname extends AbstractInputPanel {
	private JTextField jobnameTextField;

	public Jobname(Map<String, String> panelProperties) {
		super(panelProperties);
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
	protected ImmutableMap<String, String> getDefaultPanelProperties() {
		return ImmutableMap.of(TITLE, "Jobname", DEFAULT_VALUE, "gridJob_");
	}

	private JTextField getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new JTextField();
			jobnameTextField.setColumns(10);
		}
		return jobnameTextField;
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
			jobObject.setJobname(defaultValue);
		}

	}
}
