package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComboBox;

import org.vpac.grisu.frontend.view.swing.jobcreation.filters.Filter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MdsCommandline extends AbstractInputPanel {
	private JComboBox comboBox;

	private String lastCalculatedExecutable = null;

	public MdsCommandline(Map<String, String> panelProperties, LinkedList<Filter> filters) {
		super(panelProperties, filters);
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

	private void commandlineChanged() {

		String commandline;
		try {
			commandline = ((String)getComboBox().getSelectedItem()).trim();
		} catch (Exception e) {
			myLogger.debug(e.getLocalizedMessage());
			return;
		}

		String exe;
		if (commandline == null) {
			exe = "";
		} else {
			int firstWhitespace = commandline.indexOf(" ");
			if (firstWhitespace == -1) {
				exe = commandline;
			} else {
				exe = commandline.substring(0, firstWhitespace);
			}
		}

		if ((lastCalculatedExecutable != null)
				&& lastCalculatedExecutable.equals(exe)) {
			return;
		}

		lastCalculatedExecutable = exe;

		if (exe.length() == 0) {
			lastCalculatedExecutable = null;
			setValue("application", "");
			setValue("applicationVersion", "");
			setValue("commandline", "");
			return;
		}

		//		jobObject.setApplication(exe);
		setValue("commandline", commandline);

	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setEditable(true);
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if ( ItemEvent.SELECTED == e.getStateChange() ) {
						commandlineChanged();
					}
				}
			});

			comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					commandlineChanged();
				}
			});
		}
		return comboBox;
	}

	@Override
	protected Map<String, String> getDefaultPanelProperties() {

		Map<String, String> defaultProperties = new HashMap<String, String>();
		defaultProperties.put(TITLE, "Commandline");

		return defaultProperties;
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) {

		for ( String key : panelProperties.keySet() ) {
			if ( DEFAULT_VALUE.equals(key) ) {
				getComboBox().setSelectedItem(panelProperties.get(DEFAULT_VALUE));
			}
		}

	}
}
