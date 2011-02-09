package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class GlazedFileRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	private static FileSystemView fsView = FileSystemView.getFileSystemView();
	private static Icon folderIcon = fsView.getSystemIcon(new File(System
			.getProperty("user.home")));
	// TODO think of something better?
	private static Icon fileIcon = fsView.getSystemIcon(findFile());

	private static File findFile() {
		for (final File file : new File(System.getProperty("user.home"))
				.listFiles()) {
			if (file.isFile()) {
				return file;
			}
		}
		return null;
	}

	public GlazedFileRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground((Color) UIManager.get("Table.selectionBackground"));
		} else {
			setBackground(arg0.getBackground());
		}

		final GlazedFile file = (GlazedFile) arg1;

		if (!file.isMarkedAsParent()) {
			if (file.isFolder()) {
				this.setIcon(folderIcon);
			} else {
				this.setIcon(fileIcon);
			}

			try {
				this.setText(URLDecoder.decode(file.getName(), "UTF-8"));
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
				this.setText(file.getName());
			}

		} else {
			this.setIcon(null);
			this.setText("..");
		}

		return this;
	}

}
