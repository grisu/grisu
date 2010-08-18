package org.vpac.grisu.frontend.view.swing.jobmonitoring.single.appSpecific;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.FormFactory;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UnixCommands extends AppSpecificViewerPanel {
	private JSplitPane splitPane;
	private JPanel panel;
	private JLabel lblStdout;
	private JPanel panel_1;
	private JLabel lblStderr;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JScrollPane scrollPane_1;
	private JTextArea textArea_1;
	public UnixCommands() {
		super();
		setLayout(new BorderLayout(0, 0));
		add(getSplitPane());
	}

	@Override
	public void initialize() {

		getTextArea().setText(getJob().getStdOutContent());
		getTextArea_1().setText(getJob().getStdErrContent());

	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getPanel_1());
			splitPane.setRightComponent(getPanel_1_1());
			splitPane.setDividerLocation(200);
		}
		return splitPane;
	}
	private JPanel getPanel_1() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					ColumnSpec.decode("46px:grow"),
					FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.LINE_GAP_ROWSPEC,
					RowSpec.decode("14px"),
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_ROWSPEC,}));
			panel.add(getLblStdout(), "2, 2, left, top");
			panel.add(getScrollPane(), "2, 4, fill, fill");
		}
		return panel;
	}
	private JLabel getLblStdout() {
		if (lblStdout == null) {
			lblStdout = new JLabel("stdout");
		}
		return lblStdout;
	}
	private JPanel getPanel_1_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					ColumnSpec.decode("46px:grow"),
					FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
					FormFactory.LINE_GAP_ROWSPEC,
					RowSpec.decode("14px"),
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_ROWSPEC,}));
			panel_1.add(getLblStderr(), "2, 2, left, top");
			panel_1.add(getScrollPane_1(), "2, 4, fill, fill");
		}
		return panel_1;
	}
	private JLabel getLblStderr() {
		if (lblStderr == null) {
			lblStderr = new JLabel("stderr");
		}
		return lblStderr;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
		}
		return textArea;
	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getTextArea_1());
		}
		return scrollPane_1;
	}
	private JTextArea getTextArea_1() {
		if (textArea_1 == null) {
			textArea_1 = new JTextArea();
		}
		return textArea_1;
	}


}
