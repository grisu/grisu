package org.vpac.grisu.frontend.view.swing.files;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.vpac.grisu.model.files.GlazedFile;

public class FileSizeRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	public FileSizeRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1,
			boolean isSelected, boolean hasFocus, int arg4, int arg5) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(arg0.getBackground());
		}

		Long size = (Long) arg1;

		String sizeString = GlazedFile.calculateSizeString(size);

		this.setText(sizeString);
		this.setHorizontalAlignment(SwingConstants.RIGHT);

		return this;

	}

}