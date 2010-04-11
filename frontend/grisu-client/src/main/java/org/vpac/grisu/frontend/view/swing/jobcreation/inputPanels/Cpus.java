package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class Cpus extends AbstractInputPanel {
	private JCheckBox chckbxParallel;
	private JComboBox comboBox;

	/**
	 * Create the panel.
	 */
	public Cpus(JobSubmissionObjectImpl jobObject) {

		super(jobObject);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getChckbxParallel(), "4, 2, right, default");
		add(getComboBox(), "2, 4, 3, 1, fill, default");

	}

	private JCheckBox getChckbxParallel() {
		if (chckbxParallel == null) {
			chckbxParallel = new JCheckBox("Parallel");
		}
		return chckbxParallel;
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
		}
		return comboBox;
	}

	@Override
	void jobPropertyChanged(PropertyChangeEvent e) {
		// TODO Auto-generated method stub
	}


}
