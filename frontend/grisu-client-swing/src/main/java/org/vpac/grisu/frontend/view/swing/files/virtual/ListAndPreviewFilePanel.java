package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.preview.GenericGridFileViewer;
import org.vpac.grisu.model.dto.GridFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ListAndPreviewFilePanel extends JPanel {

	public enum STATE {
		Preview, Files;
	}

	private JPanel cardPanel;
	private JPanel actionPanel;
	private JComboBox comboBox;
	private GridFileTreePanel virtualFileSystemTreeTablePanel;

	private final ServiceInterface si;
	private GenericGridFileViewer genericFileViewer;

	boolean lockCombo = false;

	private final List<GridFile> roots;

	public ListAndPreviewFilePanel(ServiceInterface si) {
		this(si, null);
	}

	/**
	 * Create the panel.
	 */
	public ListAndPreviewFilePanel(ServiceInterface si, List<GridFile> roots) {
		this.si = si;
		this.roots = roots;

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getCardPanel(), "2, 2, fill, fill");
		add(getActionPanel(), "2, 4, fill, fill");

	}

	public void displayFile(GridFile file) {
		switchToPreview();
		X.p("DISPLAY: " + file.getName());
		getGenericFileViewer().setFile(file, null);
	}

	private JPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new JPanel();
			actionPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"), }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, }));
			actionPanel.add(getComboBox(), "2, 2, fill, default");
		}
		return actionPanel;
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout());
			cardPanel.add(getVirtualFileSystemTreeTablePanel(),
					STATE.Files.toString());
			cardPanel.add(getGenericFileViewer(), STATE.Preview.toString());
		}
		return cardPanel;
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new STATE[] {
					STATE.Files, STATE.Preview }));
			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					if (ItemEvent.SELECTED == e.getStateChange()) {
						if (!lockCombo) {
							switchToPanel((STATE) comboBox.getSelectedItem());
						}
					}
				}
			});
		}
		return comboBox;
	}

	private GenericGridFileViewer getGenericFileViewer() {
		if (genericFileViewer == null) {
			genericFileViewer = new GenericGridFileViewer();
			genericFileViewer.setServiceInterface(si);
		}
		return genericFileViewer;
	}

	private GridFileTreePanel getVirtualFileSystemTreeTablePanel() {
		if (virtualFileSystemTreeTablePanel == null) {
			virtualFileSystemTreeTablePanel = new GridFileTreePanel(si, roots);
		}
		return virtualFileSystemTreeTablePanel;
	}

	public void switchToFileList() {
		final CardLayout cl = (CardLayout) getCardPanel().getLayout();
		cl.show(getCardPanel(), STATE.Files.toString());
		lockCombo = true;
		getComboBox().setSelectedItem(STATE.Files);
		lockCombo = false;

	}

	public void switchToPanel(STATE panel) {
		switch (panel) {
		case Files:
			switchToFileList();
			return;
		case Preview:
			switchToPreview();
			return;
		default:
			return;

		}
	}

	public void switchToPreview() {
		final CardLayout cl = (CardLayout) getCardPanel().getLayout();
		cl.show(getCardPanel(), STATE.Preview.toString());
		lockCombo = true;
		getComboBox().setSelectedItem(STATE.Preview);
		lockCombo = false;
	}
}
