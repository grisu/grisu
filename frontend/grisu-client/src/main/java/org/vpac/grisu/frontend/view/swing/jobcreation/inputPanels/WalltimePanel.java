package org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.google.common.collect.ImmutableMap;


public class WalltimePanel extends AbstractInputPanel {

	public WalltimePanel(JobSubmissionObjectImpl jobObject, Map<String, String> panelProperties) {
		super(jobObject, panelProperties);
	}

	@Override
	protected ImmutableMap<String, String> getDefaultPanelProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void jobPropertyChanged(PropertyChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void preparePanel(Map<String, String> panelProperties) {
		// TODO Auto-generated method stub

	}

}
