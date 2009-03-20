package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.common.VersionQueuePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS extends AbstractModulePanel {

	private VersionQueuePanel versionQueuePanel;
	private CPUs us;
	private JobName jobName;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3195321397180648037L;

	/**
	 * Create the panel
	 */
	public GenericMDS() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("158dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("56dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("53dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("78dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getJobName(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		add(getUs(), new CellConstraints(4, 2, 1, 3));
		//
	}

	@Override
	protected void initialize(TemplateModule module) throws ModuleException {
		// TODO Auto-generated method stub
		add(getVersionQueuePanel(module.getTemplate().getEnvironmentManager(), module.getTemplate().getApplicationName(), true, true, true), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		
	}

	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return
	 */
	protected JobName getJobName() {
		if (jobName == null) {
			jobName = new JobName();
		}
		return jobName;
	}
	/**
	 * @return
	 */
	protected CPUs getUs() {
		if (us == null) {
			us = new CPUs();
		}
		return us;
	}
	/**
	 * @return
	 */
	protected VersionQueuePanel getVersionQueuePanel(EnvironmentManager em, String appName, boolean anyMode, boolean defaultMode, boolean exactMode) {
		if (versionQueuePanel == null) {
			versionQueuePanel = new VersionQueuePanel(em, appName, anyMode, defaultMode, exactMode);
		}
		return versionQueuePanel;
	}

}
