package grisu.frontend.view.swing.files;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import furbelow.SpinningDial;
import grisu.control.ServiceInterface;
import grisu.model.files.GlazedFile;

public class FileListActionPanel extends JPanel implements FileListListener {

	static final Logger myLogger = Logger.getLogger(FileListActionPanel.class
			.getName());

	// private static final ImageIcon REFRESH_ICON = new ImageIcon(
	// FileListActionPanel.class.getClassLoader().getResource(
	// "refresh.png"));
	//
	// private static final ImageIcon BOOKMARK_ICON = new ImageIcon(
	// FileListActionPanel.class.getClassLoader().getResource(
	// "bookmark.png"));

	private static SpinningDial LOADING_ICON = new SpinningDial(16, 16);

	private final static ImageIcon REFRESH_ICON = createImageIcon(
			"refresh.png", "Refresh");

	private final static ImageIcon BOOKMARK_ICON = createImageIcon(
			"bookmark.png", "Bookmark");

	protected static ImageIcon createImageIcon(String path, String description) {
		// java.net.URL imgURL = FileListActionPanel.class.getResource(path);
		// if (imgURL != null) {
		// return new ImageIcon(imgURL, description);
		// } else {
		// System.err.println("Couldn't find file: " + path);
		// return null;
		// }

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(FileListActionPanel.class.getClassLoader()
					.getResource(path));
		} catch (final Exception e) {
			myLogger.error(e);
		}

		return icon;

	}

	private JButton bookmarkButton;
	private JButton refreshButton;

	private final FileListPanel listPanel;
	private JLabel refreshLabel;

	private final ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public FileListActionPanel(ServiceInterface si, FileListPanel listPanel) {
		this.si = si;
		this.listPanel = listPanel;
		listPanel.addFileListListener(this);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("2dlu"), }, new RowSpec[] {
				FormFactory.NARROW_LINE_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.NARROW_LINE_GAP_ROWSPEC, }));
		add(getRefreshLabel(), "2, 2, left, default");
		add(getBookmarkButton(), "4, 2");
		add(getRefreshButton(), "6, 2");
	}

	public void directoryChanged(GlazedFile newDirectory) {
		// TODO
	}

	public void fileDoubleClicked(GlazedFile file) {
	}

	public void filesSelected(Set<GlazedFile> files) {
	}

	private JButton getBookmarkButton() {
		if (bookmarkButton == null) {
			bookmarkButton = new JButton();
			bookmarkButton.setToolTipText("Add current folder to bookmarks");
			bookmarkButton.setIcon(BOOKMARK_ICON);
			bookmarkButton.setPreferredSize(new Dimension(22, 22));
			bookmarkButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					final String alias = (String) JOptionPane.showInputDialog(
							FileListActionPanel.this,
							"Please provide the bookmark name:",
							"Add bookmark", JOptionPane.PLAIN_MESSAGE,
							BOOKMARK_ICON, null, null);

					if ((alias != null) && (alias.length() > 0)) {
						FileSystemsManager.getDefault(si).removeBookmark(alias);

						FileSystemsManager.getDefault(si).addBookmark(alias,
								listPanel.getCurrentDirectory());
					}

				}
			});
		}
		return bookmarkButton;
	}

	private JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.setToolTipText("Refresh current folder");
			refreshButton.setIcon(REFRESH_ICON);
			refreshButton.setPreferredSize(new Dimension(22, 22));
			refreshButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					listPanel.refresh();
				}

			});
		}
		return refreshButton;
	}

	private JLabel getRefreshLabel() {
		if (refreshLabel == null) {
			refreshLabel = new JLabel();
		}
		return refreshLabel;
	}

	public void isLoading(final boolean loading) {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getBookmarkButton().setEnabled(!loading);
				getRefreshButton().setEnabled(!loading);

				if (loading) {
					getRefreshLabel().setIcon(LOADING_ICON);
				} else {
					getRefreshLabel().setIcon(null);
				}
			}
		});

	}
}
