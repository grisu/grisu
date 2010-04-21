package org.vpac.grisu.frontend.view.swing.jobcreation.templates.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.BasenameFilter;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.filters.Filter;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.AbstractInputPanel;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.Cpus;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.Jobname;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.MonitorCommandlinePanel;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.MultipleInputFiles;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.SingleInputFile;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CatTemplate extends JPanel {
	private Jobname jobname;
	private Cpus cpus;

	private JobSubmissionObjectImpl jobObject;

	private ServiceInterface si;
	private MultipleInputFiles multipleInputFiles;

	private final TemplateObject template;
	private MonitorCommandlinePanel monitorCommandlinePanel;
	private SingleInputFile singleInputFile;

	/**
	 * Create the panel.
	 */
	public CatTemplate() {

		try {
			template = new TemplateObject("cat ${inputFile}");
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

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
		add(getMonitorCommandlinePanel(), "2, 2, 3, 1, fill, fill");
		add(getSingleInputFile(), "2, 4, 3, 1, fill, fill");
		add(getJobname(), "2, 6, fill, fill");
		add(getCpus(), "4, 6, fill, fill");

	}

	private Cpus getCpus() {
		if (cpus == null) {
			try {
				cpus = new Cpus(null);
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cpus;
	}

	private Jobname getJobname() {
		if (jobname == null) {
			try {
				jobname = new Jobname(null);
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jobname;
	}

	private MonitorCommandlinePanel getMonitorCommandlinePanel() {
		if (monitorCommandlinePanel == null) {
			try {
				monitorCommandlinePanel = new MonitorCommandlinePanel(null);
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return monitorCommandlinePanel;
	}

	private SingleInputFile getSingleInputFile() {
		if (singleInputFile == null) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(AbstractInputPanel.NAME, "inputFile");
			properties.put(AbstractInputPanel.TITLE, "Input file");

			Filter filter = new BasenameFilter();
			LinkedList filters = new LinkedList<Filter>();
			filters.add(filter);
			try {
				singleInputFile = new SingleInputFile(null);
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return singleInputFile;
	}
	//	public void setJobObject(JobSubmissionObjectImpl jobObject) {
	//		this.jobObject = jobObject;
	//
	//		template.setJobObject(this.jobObject);
	//		getCpus().setJobObject(this.jobObject);
	//		getJobname().setJobObject(this.jobObject);
	//		getMonitorCommandlinePanel().setJobObject(this.jobObject);
	//		getSingleInputFile().setJobObject(this.jobObject);
	//	}
	//	public void setServiceInterface(ServiceInterface si) {
	//		this.si = si;
	//		getSingleInputFile().setServiceInterface(template, this.si);
	//	}
}
