package org.vpac.grisu.client.view.swing.template.modules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.common.VersionQueuePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS extends AbstractModulePanel {

	private VersionQueuePanel versionQueuePanel;
	private CPUs cpus;
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
				ColumnSpec.decode("316px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("112px")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("86px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("36px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("75dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		//
	}

	@Override
	protected void initialize() throws ModuleException {
		add(getJobName(), new CellConstraints("2, 2, 1, 1, fill, fill"));
		add(getCPUs(), new CellConstraints("4, 2, 1, 3, fill, fill"));
		add(getVersionQueuePanel(templateModule.getTemplate().getEnvironmentManager(), templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.VERSION_TEMPLATE_TAG_NAME), templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.HOSTNAME_TEMPLATE_TAG_NAME), templateModule.getTemplate().getApplicationName()), new CellConstraints("2, 4, 1, 3, fill, fill"));
		
		try {
			getJobName().setTemplateNode(this.templateModule.getTemplateNodes().get("Jobname"));
			getCPUs().setTemplateNode(this.templateModule.getTemplateNodes().get("CPUs"));
		} catch (TemplateNodePanelException e) {

			throw new ModuleException(this.templateModule, e);
		}
	}

	public JPanel getPanel() {
		return this;
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
	protected CPUs getCPUs() {
		if (cpus == null) {
			cpus = new CPUs();
		}
		return cpus;
	}
	/**
	 * @return
	 */
	protected VersionQueuePanel getVersionQueuePanel(EnvironmentManager em, TemplateNode versionNode, TemplateNode hostNameNode, String appName) {
		if (versionQueuePanel == null) {
			versionQueuePanel = new VersionQueuePanel(em, versionNode, hostNameNode, appName);
		}
		return versionQueuePanel;
	}

}
