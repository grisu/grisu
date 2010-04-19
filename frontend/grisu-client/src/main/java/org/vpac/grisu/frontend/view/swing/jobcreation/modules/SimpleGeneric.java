package org.vpac.grisu.frontend.view.swing.jobcreation.modules;

import java.util.Map;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.Cpus;
import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.Jobname;
import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.MdsCommandline;
import org.vpac.grisu.frontend.view.swing.jobcreation.inputPanels.MultipleInputFiles;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SimpleGeneric extends JPanel {
	private Jobname jobname;
	private Cpus cpus;

	private JobSubmissionObjectImpl jobObject;
	private MdsCommandline mdsCommandline;

	private ServiceInterface si;
	private MultipleInputFiles multipleInputFiles;

	/**
	 * Create the panel.
	 */
	public SimpleGeneric() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getMdsCommandline(), "2, 2, 3, 1, fill, fill");
		add(getMultipleInputFiles(), "2, 4, 3, 1, fill, fill");
		add(getJobname(), "2, 6, fill, fill");
		add(getCpus(), "4, 6, fill, fill");

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
	private MdsCommandline getMdsCommandline() {
		if (mdsCommandline == null) {
			mdsCommandline = new MdsCommandline(null);
		}
		return mdsCommandline;
	}

	private MultipleInputFiles getMultipleInputFiles() {
		if (multipleInputFiles == null) {
			multipleInputFiles = new MultipleInputFiles((Map) null);
		}
		return multipleInputFiles;
	}
	public void setJobObject(JobSubmissionObjectImpl jobObject) {
		this.jobObject = jobObject;

		getCpus().setJobObject(this.jobObject);
		getJobname().setJobObject(this.jobObject);
		getMdsCommandline().setJobObject(this.jobObject);
		getMultipleInputFiles().setJobObject(this.jobObject);
	}
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		getMultipleInputFiles().setServiceInterface(si);
	}
}
