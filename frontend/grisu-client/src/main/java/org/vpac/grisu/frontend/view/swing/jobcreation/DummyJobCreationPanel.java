package org.vpac.grisu.frontend.view.swing.jobcreation;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class DummyJobCreationPanel extends JPanel implements JobCreationPanel {
	private JLabel lblDummy;

	/**
	 * Create the panel.
	 */
	public DummyJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		add(getLblDummy(), "2, 2, center, default");

	}

	public boolean createsBatchJob() {
		return false;
	}

	private JLabel getLblDummy() {
		if (lblDummy == null) {
			lblDummy = new JLabel("Dummy");
		}
		return lblDummy;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getSupportedApplication() {
		return "Dummy";
	}

	public void setServiceInterface(ServiceInterface si) {
		System.out.println("Serviceinterface set. DN: "+si.getDN());
	}
}
