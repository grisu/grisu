package grisu.frontend.view.swing.files.preview.fileViewers;

import grisu.frontend.view.swing.files.preview.FileViewer;
import grisu.model.files.GlazedFile;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXImageView;

public class ImageFileViewer extends JPanel implements FileViewer {

	static final Logger myLogger = Logger.getLogger(ImageFileViewer.class
			.getName());

	private JXImageView imagePanel;

	private double currentScale = 1;

	private File imageFile = null;

	/**
	 * Create the panel.
	 */
	public ImageFileViewer() {
		setLayout(new BorderLayout(0, 0));
		add(getImagePanel(), BorderLayout.CENTER);

	}

	private JXImageView getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new JXImageView();
			imagePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 2) {
						currentScale = 1;
						getImagePanel().setScale(currentScale);
					}
				}
			});
			imagePanel.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(final MouseWheelEvent e) {
					final double amount = e.getWheelRotation();
					if (amount < 0) {
						currentScale = currentScale + (currentScale * 0.10);
					} else if (amount > 0) {
						currentScale = currentScale - (currentScale / 10);
					}
					imagePanel.setScale(currentScale);
				}
			});
			imagePanel.setDragEnabled(false);
		}
		return imagePanel;
	}

	public JPanel getPanel() {
		return this;
	}

	public String[] getSupportedMimeTypes() {
		return new String[] { "image" };
	}

	public void setFile(GlazedFile file, File localFile) {

		this.imageFile = localFile;
		if (imageFile != null) {
			try {
				getImagePanel().setImage(imageFile);
			} catch (final IOException e) {
				myLogger.error(e);
			}
		}

	}
}
