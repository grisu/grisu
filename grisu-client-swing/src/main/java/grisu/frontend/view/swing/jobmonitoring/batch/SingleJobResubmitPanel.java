package grisu.frontend.view.swing.jobmonitoring.batch;

import grisu.frontend.model.job.BatchJobObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jidesoft.swing.CheckBoxList;

public class SingleJobResubmitPanel extends JPanel {

	private JRadioButton rdbtnResubmitToAll;
	private JRadioButton rdbtnManuallySelectResubmitlocations;
	private CheckBoxList checkBoxList;

	private final DefaultListModel manualModel = new DefaultListModel();

	private final ButtonGroup buttonGroup;

	private final BatchJobObject bj;
	private final Set<String> jobnamesToResubmit;

	/**
	 * Create the panel.
	 */
	public SingleJobResubmitPanel(BatchJobObject bj,
			Set<String> jobnamesToResubmit) {
		this.bj = bj;
		this.jobnamesToResubmit = jobnamesToResubmit;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));

		buttonGroup = new ButtonGroup();

		for (final String subLoc : bj.getCurrentlyUsedSubmissionLocations()) {
			manualModel.addElement(subLoc);
		}

		add(getRdbtnResubmitToAll(), "2, 2, 5, 1");
		add(getRdbtnManuallySelectResubmitlocations(), "2, 4, 5, 1");
		add(getCheckBoxList(), "4, 6, 3, 1, fill, fill");

		getRdbtnResubmitToAll().setSelected(true);

	}

	private void enableManualSelection(boolean enable) {

		if (enable) {
			getCheckBoxList().setEnabled(true);
		} else {
			getCheckBoxList().setEnabled(false);
		}

	}

	private CheckBoxList getCheckBoxList() {
		if (checkBoxList == null) {
			checkBoxList = new CheckBoxList(manualModel);
		}
		return checkBoxList;
	}

	private JRadioButton getRdbtnManuallySelectResubmitlocations() {
		if (rdbtnManuallySelectResubmitlocations == null) {
			rdbtnManuallySelectResubmitlocations = new JRadioButton(
					"Manually select resubmit-locations:");
			rdbtnManuallySelectResubmitlocations
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							enableManualSelection(true);
						}
					});
			buttonGroup.add(rdbtnManuallySelectResubmitlocations);
		}
		return rdbtnManuallySelectResubmitlocations;
	}

	private JRadioButton getRdbtnResubmitToAll() {
		if (rdbtnResubmitToAll == null) {
			rdbtnResubmitToAll = new JRadioButton(
					"Resubmit to all possible locations");
			rdbtnResubmitToAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					enableManualSelection(false);
				}
			});
			buttonGroup.add(rdbtnResubmitToAll);
		}
		return rdbtnResubmitToAll;
	}

	public Set<String> getSubmissionLocations() {

		if (getRdbtnResubmitToAll().isSelected()) {
			return bj.getCurrentlyUsedSubmissionLocations();
		} else {
			final HashSet<String> subLocs = new HashSet<String>();
			for (final Object subLoc : getCheckBoxList()
					.getCheckBoxListSelectedValues()) {
				subLocs.add((String) subLoc);
			}
			return subLocs;
		}

	}
}
