package org.vpac.grisu.frontend.view.swing.jobcreation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.modules.CatTemplate;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TestModuleJobCreationPanel extends JPanel implements JobCreationPanel {
	private ServiceInterface si;

	private UserEnvironmentManager em;
	private CatTemplate simpleGeneric;
	private JButton button;

	private JobObject jobObject;

	public TestModuleJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getCatGeneric(), "2, 2, fill, fill");
		add(getButton(), "2, 4");
	}

	public boolean createsBatchJob() {
		return false;
	}

	public boolean createsSingleJob() {
		return true;
	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Submit");
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					try {
						jobObject.createJob("/ARCS/NGAdmin");
					} catch (JobPropertiesException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						jobObject.submitJob();
					} catch (JobSubmissionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return button;
	}

	private CatTemplate getCatGeneric() {
		if (simpleGeneric == null) {
			simpleGeneric = new CatTemplate();
		}
		return simpleGeneric;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getPanelName() {
		return "TestModule";
	}

	public String getSupportedApplication() {
		return "UnixCommands";
	}
	public void setServiceInterface(ServiceInterface si) {
		System.out.println("Serviceinterface set. DN: "+si.getDN());
		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();

		jobObject = new JobObject(this.si);
		jobObject.setApplication("UnixCommands");
		//		getCatGeneric().setServiceInterface(si);
		//		getCatGeneric().setJobObject(jobObject);

	}
}
