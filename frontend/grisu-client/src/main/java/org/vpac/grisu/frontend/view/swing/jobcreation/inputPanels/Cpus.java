package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.google.common.collect.ImmutableMap;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class Cpus extends AbstractInputPanel {

	private final String FORCE_MPI = "force_mpi";
	private final String FORCE_SINGLE = "force_single";

	private JCheckBox chckbxParallel;
	private JComboBox comboBox;

	/**
	 * @wbp.parser.constructor
	 */
	public Cpus(JobSubmissionObjectImpl jobObject) {

		this(jobObject, null);

	}

	public Cpus(JobSubmissionObjectImpl jobObject, Map<String, String>panelProperties) {

		super(jobObject, panelProperties);
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
			chckbxParallel.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					if ( e.getStateChange() == ItemEvent.SELECTED ) {
						jobObject.setForce_mpi(true);
					} else {
						jobObject.setForce_single(true);
					}
				}
			});
		}
		return chckbxParallel;
	}

	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {

					if ( ItemEvent.SELECTED == e.getStateChange() ) {
						Integer value = (Integer)getComboBox().getSelectedItem();
						jobObject.setCpus(value);
					}

				}
			});
		}
		return comboBox;
	}

	@Override
	protected ImmutableMap<String, String> getDefaultPanelProperties() {

		return ImmutableMap.of("defaultCpuList", "1,2,4,8,16,32", "forceSingle", "false", "forceMpi", "false");
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {

		if ( "cpus".equals(e.getPropertyName()) ) {
			int value = (Integer)e.getNewValue();
			getComboBox().setSelectedItem(value);
		} else if ( "force_single".equals(e.getPropertyName()) ) {
			getChckbxParallel().setSelected(false);
		} else if ( "force_mpi".equals(e.getPropertyName()) ) {
			getChckbxParallel().setSelected(true);
		}

	}


	@Override
	protected void preparePanel(Map<String, String> panelProperties) {

		for ( String key : panelProperties.keySet() ) {
			try {
				if ( DEFAULT_VALUE.equals(key) ) {
					jobObject.setCpus(Integer.parseInt(panelProperties.get(DEFAULT_VALUE)));
				} else if (PREFILLS.equals(key)) {
					for ( String item : panelProperties.get(PREFILLS).split(",") ) {
						getComboBox().addItem(Integer.parseInt(item));
					}
				} else if ( FORCE_MPI.equals(key) ) {
					if ( Boolean.parseBoolean(panelProperties.get(FORCE_MPI)) ) {
						jobObject.setForce_mpi(true);
					}
				} else if ( FORCE_SINGLE.equals(key) ) {
					if ( Boolean.parseBoolean(panelProperties.get(FORCE_SINGLE)) ) {
						jobObject.setForce_single(true);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


}
