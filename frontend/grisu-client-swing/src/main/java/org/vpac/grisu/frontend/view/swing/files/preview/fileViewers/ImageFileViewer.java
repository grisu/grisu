package org.vpac.grisu.frontend.view.swing.files.preview.fileViewers;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXImageView;
import org.vpac.grisu.frontend.view.swing.files.preview.FileViewer;
import org.vpac.grisu.model.files.GlazedFile;

public class ImageFileViewer extends JPanel implements FileViewer {
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
						currentScale = currentScale + currentScale * 0.10;
					} else if (amount > 0) {
						currentScale = currentScale - currentScale / 10;
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
				e.printStackTrace();
			}
		}

	}
}
