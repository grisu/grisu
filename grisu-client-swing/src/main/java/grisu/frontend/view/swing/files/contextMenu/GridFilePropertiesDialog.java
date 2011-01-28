package grisu.frontend.view.swing.files.contextMenu;

import grisu.model.FileManager;
import grisu.model.dto.DtoProperty;
import grisu.model.dto.GridFile;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;

public class GridFilePropertiesDialog extends JDialog {

	public static String generateHtml(GridFile file) {

		final StringBuffer html = new StringBuffer(
		"<html><table width=\"100%\">");

		boolean alternate = true;

		Map<String, String> fileProperties = new LinkedHashMap<String, String>();

		if (file.isInaccessable()) {
			fileProperties.put("Access error", "");
			fileProperties.put("Site", StringUtils.join(file.getSites(), ", "));
			fileProperties.put("URL", file.getUrl());
			fileProperties.put("Reason", file.getComment());
		} else {
			fileProperties.put("Name", file.getName());
			String bytes = null;
			if (file.getSize() <= 0) {
				bytes = "";
			} else {
				bytes = " (" + file.getSize() + " b)";
			}
			fileProperties.put("Size",
					FileManager.calculateSizeString(file.getSize()) + bytes);
			fileProperties.put("Last modified",
					FileManager.getLastModifiedString(file.getLastModified()));
			fileProperties.put("URL", file.getUrl());
			Set<String> add = DtoProperty.mapFromDtoPropertiesList(file.getUrls())
			.keySet();
			add.remove(file.getUrl());
			fileProperties.put("Additional urls", StringUtils.join(add, "<br>"));
			fileProperties.put("Path", file.getPath());
			fileProperties.put("Sites", StringUtils.join(file.getSites(), "<br>"));
			fileProperties.put("Groups", StringUtils.join(file.getFqans(), "<br>"));
			fileProperties.put("Virtual", Boolean.toString(file.isVirtual()));
		}
		for (final String key : fileProperties.keySet()) {
			if (alternate) {
				html.append("<tr bgcolor=\"#FFFFFF\"><td>");
			} else {
				html.append("<tr><td>");
			}
			html.append(key);
			html.append("</td><td>");
			html.append(fileProperties.get(key));
			html.append("</td></tr>");
			alternate = !alternate;
		}
		html.append("</table></html>");

		return html.toString();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			GridFilePropertiesDialog dialog = new GridFilePropertiesDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final JPanel contentPanel = new JPanel();
	private final JEditorPane editorPane = new JEditorPane();

	/**
	 * Create the dialog.
	 */
	public GridFilePropertiesDialog(Frame parent) {
		super(parent);
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				scrollPane.setViewportView(editorPane);
			}
		}
		// {
		// JPanel buttonPane = new JPanel();
		// buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// getContentPane().add(buttonPane, BorderLayout.SOUTH);
		// {
		// JButton okButton = new JButton("OK");
		// okButton.setActionCommand("OK");
		// buttonPane.add(okButton);
		// getRootPane().setDefaultButton(okButton);
		// }
		// }
	}

	public void setGridFile(GridFile f) {

		String text = generateHtml(f);
		editorPane.setText(text);
		setVisible(true);
	}

}
