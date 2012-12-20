package grisu.frontend.view.swing.jobmonitoring.single;

import grisu.frontend.model.job.JobObject;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class JobNameCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {
	
	private int[] disabledRows = new int[]{};

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(table.getBackground());
		}

		final JobObject j = (JobObject) value;

		if (j == null) {
			final Component c = super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			c.setEnabled(false);
			setText("n/a");
		} else if (j.isBeingCleaned()) {
			final Component c = super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			c.setEnabled(false);

			setText(j.getJobname() + " (being cleaned)");
		} else {
			final Component c = super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			c.setEnabled(true);
			setText(j.getJobname());
		}
		

		return this;

	}

	public void disableRows(int[] selectedRows) {
		
		this.disabledRows = selectedRows;
		
	}

}
