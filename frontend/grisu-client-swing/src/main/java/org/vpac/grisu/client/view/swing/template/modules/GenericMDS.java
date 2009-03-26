package org.vpac.grisu.client.view.swing.template.modules;

import java.util.Vector;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.modules.SubmissionObjectHolder;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.common.VersionQueuePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;
import org.vpac.grisu.control.JobCreationException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS extends AbstractModulePanel implements SubmissionObjectHolder {

	private WallTime wallTime;
	private MemoryInputPanel memoryInputPanel;
	private Email email;
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
				ColumnSpec.decode("140px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("75dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("152px")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("86px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("90px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("75dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		//
	}

	@Override
	protected void initialize() throws ModuleException {
		add(getJobName(), new CellConstraints("2, 2, 3, 1, fill, fill"));
		add(getCPUs(), new CellConstraints("6, 2, 1, 3, fill, fill"));
		add(getVersionQueuePanel(templateModule.getTemplate().getEnvironmentManager(), templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.VERSION_TEMPLATE_TAG_NAME), templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.HOSTNAME_TEMPLATE_TAG_NAME), templateModule.getTemplate().getApplicationName()), new CellConstraints("2, 6, 3, 1, fill, fill"));
		
		try {
			getJobName().setTemplateNode(this.templateModule.getTemplateNodes().get("Jobname"));
			getCPUs().setTemplateNode(this.templateModule.getTemplateNodes().get("CPUs"));
		} catch (TemplateNodePanelException e) {

			throw new ModuleException(this.templateModule, e);
		}
		add(getEmail(), new CellConstraints(2, 8, 5, 1));
		add(getMemoryInputPanel(), new CellConstraints(4, 4, CellConstraints.DEFAULT, CellConstraints.TOP));
		add(getWallTime(), new CellConstraints(2, 4));
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
	/**
	 * @return
	 */
	protected Email getEmail() {
		if (email == null) {
			email = new Email();
		}
		return email;
	}
	/**
	 * @return
	 */
	protected MemoryInputPanel getMemoryInputPanel() {
		if (memoryInputPanel == null) {
			memoryInputPanel = new MemoryInputPanel();
		}
		return memoryInputPanel;
	}
	/**
	 * @return
	 */
	protected WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
	}
	
	// event stuff

	private Vector<SubmissionObjectListener> submissionObjectListener;
	
	public void addSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListener == null)
			submissionObjectListener = new Vector();
		submissionObjectListener.addElement(l);
	}

	public SubmissionObject getCurrentSubmissionObject()
			throws JobCreationException {

		return getV
		
	}

	public void removeSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListener == null) {
			submissionObjectListener = new Vector<SubmissionObjectListener>();
		}
		submissionObjectListener.removeElement(l);
	}

}
