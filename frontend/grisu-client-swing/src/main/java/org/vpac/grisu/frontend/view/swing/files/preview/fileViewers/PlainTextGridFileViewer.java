package org.vpac.grisu.frontend.view.swing.files.preview.fileViewers;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.vpac.grisu.frontend.view.swing.files.preview.GridFileViewer;
import org.vpac.grisu.model.dto.GridFile;

public class PlainTextGridFileViewer extends JPanel implements GridFileViewer {
	private JScrollPane scrollPane;
	private JTextArea textArea;

	public PlainTextGridFileViewer() {
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane(), BorderLayout.CENTER);
	}

	public JPanel getPanel() {
		return this;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}

	public String[] getSupportedMimeTypes() {
		return new String[] { "text" };
	}

	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setMargin(new Insets(10, 10, 10, 10));
			textArea.setEditable(false);
		}
		return textArea;
	}

	public void setFile(GridFile file, File localCacheFile) {

		String text = null;
		try {
			text = FileUtils.readFileToString(localCacheFile);
		} catch (final IOException e) {
			e.printStackTrace();
			text = "Could not read file:\n\n" + e.getLocalizedMessage();
		}

		getTextArea().setText(text);
		getTextArea().setCaretPosition(0);

	}
}
