package org.vpac.grisu.frontend.view.swing.jobmonitoring.single;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.vpac.grisu.frontend.model.job.JobObject;

public class JobNameCellRenderer extends DefaultTableCellRenderer implements
TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(table.getBackground());
		}

		JobObject j = (JobObject)value;

		if ( j == null ) {
			Component c = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			c.setEnabled(false);
			setText("n/a");
		} else if ( j.isBeingCleaned() ) {
			Component c = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			c.setEnabled(false);

			setText(j.getJobname()+" (being cleaned)");
		} else {
			Component c = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			c.setEnabled(true);
			setText(j.getJobname());
		}

		return this;

	}




}
