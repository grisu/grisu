package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import java.util.Comparator;

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class GlazedFileTableFormat implements AdvancedTableFormat<GlazedFile> {

	private final GlazedFileComparator glazedFileComparator = new GlazedFileComparator();

	public Class getColumnClass(int arg0) {

		switch (arg0) {
		case 0:
			return GlazedFile.class;
		case 1:
			return Long.class;
		case 2:
			return Long.class;
		}

		throw new IllegalStateException();
	}

	public Comparator getColumnComparator(int arg0) {

		switch (arg0) {
		case 0:
			return glazedFileComparator;
		case 1:
			return null;
		case 2:
			return null;
		}

		throw new IllegalStateException();
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int arg0) {

		switch (arg0) {
		case 0:
			return "Name";
		case 1:
			return "Size";
		case 2:
			return "Date modified";
		}

		throw new IllegalStateException();

	}

	public Object getColumnValue(GlazedFile arg0, int arg1) {

		switch (arg1) {
		case 0:
			return arg0;
		case 1:
			return new Long(arg0.getSize());
		case 2:
			return new Long(arg0.getLastModified());
		}

		throw new IllegalStateException();

	}

}
