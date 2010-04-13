package org.vpac.grisu.frontend.view.swing.jobcreation.modules;

import javax.swing.JPanel;

import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.Cpus;
import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.Jobname;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SimpleGeneric extends JPanel {
	private Jobname jobname;
	private Cpus cpus;

	private JobSubmissionObjectImpl jobObject;

	/**
	 * Create the panel.
	 */
	public SimpleGeneric() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getJobname(), "2, 2, fill, fill");
		add(getCpus(), "4, 2, fill, fill");

	}

	private Cpus getCpus() {
		if (cpus == null) {
			cpus = new Cpus(null);
		}
		return cpus;
	}

	private Jobname getJobname() {
		if (jobname == null) {
			jobname = new Jobname(null);
		}
		return jobname;
	}
	public void setJobObject(JobSubmissionObjectImpl jobObject) {
		this.jobObject = jobObject;

		getCpus().setJobObject(this.jobObject);
		getJobname().setJobObject(this.jobObject);
	}
}
