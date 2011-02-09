package grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class BatchJobStatusCellRenderer extends JProgressBar implements
		TableCellRenderer {

	public BatchJobStatusCellRenderer() {
		this.setMinimum(0);
		this.setMaximum(100);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(table.getBackground());
		}

		final Integer status = (Integer) value;
		if (status <= 0) {
			this.setEnabled(false);
			this.setStringPainted(false);
			this.setValue(0);
		} else {
			this.setEnabled(true);
			this.setStringPainted(true);
			this.setValue(status);
		}

		return this;

	}

}
