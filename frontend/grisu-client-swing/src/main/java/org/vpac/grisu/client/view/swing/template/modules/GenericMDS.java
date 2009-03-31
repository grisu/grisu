package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.Version;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS extends AbstractModulePanel {

	private Version version;
	private Email email;
	private MemoryInputPanel memoryInputPanel;
	private CPUs us;
	private WallTime wallTime;
	private JobName jobName;
	/**
	 * Create the panel
	 */
	public GenericMDS() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("126dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("113dlu")},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("56dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("73dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getJobName(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		add(getWallTime(), new CellConstraints(4, 2));
		add(getUs(), new CellConstraints(4, 4));
		add(getMemoryInputPanel(), new CellConstraints(4, 6));
		add(getEmail(), new CellConstraints(2, 8, 3, 1));
		add(getVersion(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		//
	}

	@Override
	protected void initialize() throws ModuleException {
		// TODO Auto-generated method stub
		
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
	protected WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
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
	protected MemoryInputPanel getMemoryInputPanel() {
		if (memoryInputPanel == null) {
			memoryInputPanel = new MemoryInputPanel();
		}
		return memoryInputPanel;
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
	protected Version getVersion() {
		if (version == null) {
			version = new Version();
		}
		return version;
	}

}
