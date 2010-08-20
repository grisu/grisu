package org.vpac.grisu.frontend.view.swing.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vpac.grisu.model.files.FileSystemItem;

public class FirstItemPromptItemRenderer extends DefaultListCellRenderer implements
		ListCellRenderer {
	
	private final String selectionPrompt;
	
	public FirstItemPromptItemRenderer(String selPrompt) {
		this.selectionPrompt = selPrompt;
	}

	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean arg3, boolean arg4) {

		if (arg1 == null) {
			return this;
		}
		String item = (String) arg1;

		this.setText(item);

		if (this.selectionPrompt.equals(item)) {
			this.setHorizontalAlignment(SwingConstants.CENTER);
			setEnabled(false);
		} else {
			this.setHorizontalAlignment(SwingConstants.LEFT);
			setEnabled(true);
		}

		if (arg3 && !this.selectionPrompt.equals(item)) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(arg0.getBackground());
		}

		return this;

	}

}
