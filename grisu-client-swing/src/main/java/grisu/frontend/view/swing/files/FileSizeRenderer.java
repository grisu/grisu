package grisu.frontend.view.swing.files;

import grisu.jcommons.utils.FileAndUrlHelpers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

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

		final Long size = (Long) arg1;

		final String sizeString = FileAndUrlHelpers.calculateSizeString(size);

		this.setText(sizeString);
		this.setHorizontalAlignment(SwingConstants.RIGHT);

		return this;

	}

}
