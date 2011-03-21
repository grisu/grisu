package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.control.ServiceInterface;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class GridFileComboboxRenderer extends DefaultListCellRenderer {

	private final ServiceInterface si;

	protected ListCellRenderer backend;
	// private final static Dimension preferredSize = new Dimension(0, 24);

	private final FileManager fm;

	public GridFileComboboxRenderer(ServiceInterface si,
			ListCellRenderer backend) {
		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
		this.backend = backend;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		Component c = backend.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);

		if ((c instanceof JLabel) == false) {
			c = super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
		}


		// Font theFont = null;
		// Color theForeground = null;
		// Icon theIcon = null;
		// String theText = null;
		// String tooltip = null;

		if (value instanceof GridFile) {

			GridFile f = (GridFile) value;

			// try {
			if (f.isInaccessable()
					|| !(FileManager.localFileExists(f.getUrl()))) {
				// theForeground = list.getForeground();
				// theText = f.getName();
				// tooltip = f.getUrl();
				// } else {
				// theForeground = Color.RED;
				// theText = f.getName() + " (unavailable)";
				// tooltip = f.getUrl() + " (unavailable)";
				c.setForeground(Color.RED);
				// c.setEnabled(false);

			}
			// } catch (RemoteFileSystemException e) {
			// // not important for now
			// e.printStackTrace();
			// }

			// } else {
			// theFont = list.getFont();
			// theForeground = list.getForeground();
			// theText = "n/a";
		}
		// if (!isSelected) {
		// c.setForeground(theForeground);
		// }
		// if (theIcon != null) {
		// c.setIcon(theIcon);
		// }

		// c.setText(theText);
		// c.setToolTipText(tooltip);
		// renderer.setFont(theFont);

		return c;

	}

}
