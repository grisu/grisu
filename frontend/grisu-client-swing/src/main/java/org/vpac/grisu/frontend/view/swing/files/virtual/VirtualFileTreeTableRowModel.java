package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.util.Date;

import javax.swing.tree.DefaultMutableTreeNode;

import org.netbeans.swing.outline.RowModel;
import org.vpac.grisu.model.dto.GridFile;

public class VirtualFileTreeTableRowModel implements RowModel {

	public Class getColumnClass(int col) {
		switch (col) {
		case 0:
			return GridFile.class;
		case 1:
			return String.class;
		default:
			return null;
		}

	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Date";
		case 1:
			return "Size";
		default:
			return null;
		}
	}

	public Object getValueFor(Object arg0, int col) {

		Object userObject = ((DefaultMutableTreeNode) arg0).getUserObject();

		if (userObject instanceof GridFile) {
			GridFile f = (GridFile) userObject;
			switch (col) {
			case 0:
				Long date = f.getLastModified();
				if (date <= 0) {
					return "";
				} else {
					return new Date(date).toString();
				}
			case 1:
				Long size = f.getSize();
				if (size < 0) {
					return "";
				} else {
					return size.toString();
				}

			default:
				return null;
			}
		} else if (userObject instanceof String) {

			String s = (String) userObject;
			switch (col) {
			default:
				return "";
			}

		} else {
			throw new RuntimeException("Don't know userObject for table");
		}
	}

	public boolean isCellEditable(Object arg0, int arg1) {
		return false;
	}

	public void setValueFor(Object arg0, int arg1, Object arg2) {
		// nothing to do
	}

}
