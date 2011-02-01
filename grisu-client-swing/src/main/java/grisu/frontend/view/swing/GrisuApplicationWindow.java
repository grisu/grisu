package grisu.frontend.view.swing;

import grisu.GrisuVersion;
import grisu.X;
import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.model.events.ApplicationEventListener;
import grisu.frontend.view.swing.jobcreation.JobCreationPanel;
import grisu.frontend.view.swing.login.LoginPanel;
import grisu.frontend.view.swing.login.ServiceInterfaceHolder;
import grisu.frontend.view.swing.utils.AdvancedSettingsPanel;
import grisu.jcommons.utils.HttpProxyPanel;
import grisu.model.dto.GridFile;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;


import com.google.common.collect.ImmutableList;

public abstract class GrisuApplicationWindow implements WindowListener,
		ServiceInterfaceHolder {

	private ServiceInterface si;

	private GrisuMainPanel mainPanel;

	private LoginPanel lp;
	protected final GrisuMenu menu;

	private JXFrame frame;

	private final HttpProxyPanel httpProxyPanel = new HttpProxyPanel();

	private final Set<ServiceInterfacePanel> configPanels;

	public GrisuApplicationWindow() {
		this((ServiceInterfacePanel) null);
	}

	public GrisuApplicationWindow(ServiceInterfacePanel panel) {

		initialize();

		menu = new GrisuMenu(this);
		getFrame().setJMenuBar(menu);

		if (panel == null) {
			this.configPanels = new HashSet<ServiceInterfacePanel>();
			this.configPanels.add(new AdvancedSettingsPanel());
			addSettingsPanel("Advanced", this.configPanels.iterator().next()
					.getPanel());
		} else {
			this.configPanels = new HashSet<ServiceInterfacePanel>();
			this.configPanels.add(panel);
			for (ServiceInterfacePanel p : configPanels) {
				addSettingsPanel(p.getPanelTitle(), p.getPanel());
			}
		}

	}

	/**
	 * Launch the application.
	 */
	public GrisuApplicationWindow(Set<ServiceInterfacePanel> configpanels) {

		initialize();

		menu = new GrisuMenu(this);
		getFrame().setJMenuBar(menu);

		if (configpanels == null) {
			this.configPanels = new HashSet<ServiceInterfacePanel>();
			this.configPanels.add(new AdvancedSettingsPanel());
			addSettingsPanel("Advanced", this.configPanels.iterator().next()
					.getPanel());
		} else {
			this.configPanels = configpanels;
			for (ServiceInterfacePanel panel : configPanels) {
				addSettingsPanel(panel.getPanelTitle(), panel.getPanel());
			}
		}
		addSettingsPanel("Http proxy settings", httpProxyPanel);

	}

	public void addDefaultFileNavigationTaskPane() {
		mainPanel.addDefaultFileNavigationTaskPane();
	}

	public void addGroupFileListPanel(List<GridFile> left, List<GridFile> right) {
		mainPanel.addGroupFileListPanel(left, right);
	}

	public void addSettingsPanel(String name, JPanel panel) {
		menu.addSettingsPanel(name, panel);
	}

	abstract public boolean displayAppSpecificMonitoringItems();

	abstract public boolean displayBatchJobsCreationPane();

	abstract public boolean displaySingleJobsCreationPane();

	public void exit() {
		try {
			X.p("Exiting...");

			if (si != null) {
				String temp = si.logout();
				X.p(temp);
			}

		} finally {
			WindowSaver.saveSettings();
			System.exit(0);
		}
	}

	public Set<String> getApplicationsToMonitor() {

		Set<String> result = new TreeSet<String>();
		for (JobCreationPanel panel : getJobCreationPanels()) {
			result.add(panel.getSupportedApplication());
		}
		return result;
	}

	public JFrame getFrame() {
		return frame;
	}

	abstract public JobCreationPanel[] getJobCreationPanels();

	abstract public String getName();

	public ServiceInterface getServiceInterface() {
		return si;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		LoginManager.initEnvironment();

		new ApplicationEventListener();

		final Toolkit tk = Toolkit.getDefaultToolkit();
		tk.addAWTEventListener(WindowSaver.getInstance(),
				AWTEvent.WINDOW_EVENT_MASK);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		frame = new JXFrame();
		String title = null;
		String clientVersion = GrisuVersion.get("this-client");
		if (StringUtils.containsIgnoreCase(clientVersion, "snapshot")) {
			title = getName() + "  (DEVELOPMENT VERSION)";
		} else {
			// title = getName() + "v" + clientVersion;
			title = getName();
		}
		frame.setTitle(title);
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		final boolean singleJobs = displaySingleJobsCreationPane();
		final boolean batchJobs = displayBatchJobsCreationPane();

		final boolean displayAppSpecificMonitoringItems = displayAppSpecificMonitoringItems();

		if (displayAppSpecificMonitoringItems) {
			mainPanel = new GrisuMainPanel(singleJobs, false, true,
					getApplicationsToMonitor(), batchJobs, false, true,
					getApplicationsToMonitor());
		} else {
			mainPanel = new GrisuMainPanel(singleJobs, true, false,
					getApplicationsToMonitor(), batchJobs, true, false,
					getApplicationsToMonitor());
		}

		final List<ServiceInterfaceHolder> siHolders = ImmutableList
				.of((ServiceInterfaceHolder) this);
		final LoginPanel lp = new LoginPanel(mainPanel, siHolders);
		frame.getContentPane().add(lp, BorderLayout.CENTER);
	}

	abstract protected void initOptionalStuff(ServiceInterface si);

	public void refreshJobCreationPanels() {

		mainPanel.removeAlJobCreationPanelsl();
		for (final JobCreationPanel panel : getJobCreationPanels()) {
			mainPanel.addJobCreationPanel(panel);
		}

	}

	public void setServiceInterface(ServiceInterface si) {

		this.si = si;
		this.menu.setServiceInterface(si);
		initOptionalStuff(si);
		refreshJobCreationPanels();

		for (ServiceInterfacePanel panel : configPanels) {
			panel.setServiceInterface(si);
		}

	}

	public void setServiceInterfaceExternal(ServiceInterface si) {

		if (lp == null) {
			throw new IllegalStateException("LoginPanel not initialized.");
		}

		if (si == null) {
			throw new NullPointerException("ServiceInterface can't be null");
		}
		// this.si = si;
		lp.setServiceInterface(si);

	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent arg0) {
		exit();
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

}
