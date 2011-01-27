package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.dto.DtoProperty;
import org.vpac.grisu.model.dto.GridFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class DropVirtualGridFilePanel extends JPanel implements ActionListener {
	private JLabel lblMultiblePossibleTargets;
	private JPanel panel;
	private GridFile gridFile;

	private final ButtonGroup group = new ButtonGroup();
	private JScrollPane scrollPane;

	private String sel;

	/**
	 * Create the panel.
	 */
	public DropVirtualGridFilePanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLblMultiblePossibleTargets(), "2, 2, 3, 1, left, default");
		add(getScrollPane(), "2, 4, 3, 1, fill, fill");

	}

	public void actionPerformed(ActionEvent e) {

		this.sel = e.getActionCommand();

	}

	private void addTargetUrl(String url) {

		if (url.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME)) {
			return;
		}

		JRadioButton b = new JRadioButton(url);
		b.setToolTipText(url);
		b.setActionCommand(url);
		b.addActionListener(this);
		group.add(b);
		getPanel().add(b);

	}

	private JLabel getLblMultiblePossibleTargets() {
		if (lblMultiblePossibleTargets == null) {
			lblMultiblePossibleTargets = new JLabel(
					"Multible possible targets, please select one:");
		}
		return lblMultiblePossibleTargets;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		}
		return panel;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}

	public String getSelectedTarget() {
		return sel;
	}

	public void setTargetGridFile(GridFile f) {

		this.gridFile = f;
		for (String u : DtoProperty.mapFromDtoPropertiesList(
				this.gridFile.getUrls()).keySet()) {
			addTargetUrl(u);
		}

	}
}
