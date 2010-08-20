package org.vpac.grisu.frontend.view.swing.files;

import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileDetailPanel extends JPanel implements FileListListener {

	private static final String NAME_PREFIX = "Filename: ";
	private static final String SIZE_PREFIX = "Size: ";
	private static final String DATE_PREFIX = "Last modified: ";

	private JLabel sizeLabel;
	private JLabel timestampLabel;
	private JLabel nameLabel;

	/**
	 * Create the panel.
	 */
	public FileDetailPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getNameLabel(), "2, 2");
		add(getSizeLabel(), "2, 4");
		add(getTimestampLabel(), "2, 6");

	}

	public void directoryChanged(GlazedFile newDirectory) {
		// TODO Auto-generated method stub

	}

	public void fileDoubleClicked(GlazedFile file) {

		setFile(file);
	}

	public void filesSelected(Set<GlazedFile> files) {

		if ((files == null) || (files.size() == 0) || (files.size() > 1)) {
			setFile(null);
		} else {
			GlazedFile sel = files.iterator().next();
			if (sel.isFolder()) {
				setFile(null);
			} else {
				setFile(sel);
			}
		}

	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
		}
		return nameLabel;
	}

	private JLabel getSizeLabel() {
		if (sizeLabel == null) {
			sizeLabel = new JLabel();
		}
		return sizeLabel;
	}

	private JLabel getTimestampLabel() {
		if (timestampLabel == null) {
			timestampLabel = new JLabel();
		}
		return timestampLabel;
	}

	public void isLoading(boolean loading) {

		setFile(null);
	}

	public void setFile(final GlazedFile file) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				if (file == null) {
					getNameLabel().setText(null);
					getSizeLabel().setText(null);
					getTimestampLabel().setText(null);
				} else {
					getNameLabel().setText(NAME_PREFIX + file.getName());
					getSizeLabel().setText(
							SIZE_PREFIX
									+ GlazedFile.calculateSizeString(file
											.getSize()));
					getTimestampLabel().setText(
							DATE_PREFIX
									+ GlazedFile.calculateTimeStampString(file
											.getLastModified()));
				}
			}
		});
	}
}
