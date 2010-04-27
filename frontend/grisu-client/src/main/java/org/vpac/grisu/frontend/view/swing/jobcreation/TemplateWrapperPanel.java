package org.vpac.grisu.frontend.view.swing.jobcreation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TemplateWrapperPanel extends JPanel {
	private JPanel panel;
	private JButton button;

	private final TemplateObject template;

	/**
	 * Create the panel.
	 */
	public TemplateWrapperPanel(TemplateObject template) {
		this.template = template;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(template.getTemplatePanel(), "2, 2, fill, fill");
		add(getButton(), "2, 4, right, default");

	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Submit");
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					template.submitJob();

				}
			});
		}
		return button;
	}

}
