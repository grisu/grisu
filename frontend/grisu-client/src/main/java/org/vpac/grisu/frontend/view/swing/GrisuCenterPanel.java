package org.vpac.grisu.frontend.view.swing;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.batch.MultiBatchJobMonitoringGrid;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.MultiSingleJobMonitoringGrid;

public class GrisuCenterPanel extends JPanel {

	public static final String LOADING_PANEL = "loading";
	public static final String SINGLE_JOB_MONITORING_GRID = "singleJobMontitoring";
	public static final String BATCH_JOB_MONITORING_GRID = "batchJobMonitoring";

	private MultiSingleJobMonitoringGrid multiSingleJobMonitoringGrid;
	private MultiBatchJobMonitoringGrid multiBatchJobMonitoringGrid;

	private final ServiceInterface si;
	private LoadingPanel loadingPanel;

	/**
	 * Create the panel.
	 */
	public GrisuCenterPanel(ServiceInterface si) {
		this.si = si;
		setLayout(new CardLayout(0, 0));
		add(getLoadingPanel(), LOADING_PANEL);
		add(getMultiSingleJobMonitoringGrid(), SINGLE_JOB_MONITORING_GRID);
		add(getMultiBatchJobMonitoringGrid(), BATCH_JOB_MONITORING_GRID);

	}

	public void displayBatchJobGrid(String application) {

		final CardLayout cl = (CardLayout)(getLayout());

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(GrisuCenterPanel.this, LOADING_PANEL);
			}
		});

		getMultiBatchJobMonitoringGrid().displayGridForApplication(application);

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(GrisuCenterPanel.this, BATCH_JOB_MONITORING_GRID);
			}
		});
	}

	public void displaySingleJobGrid(String application) {

		final CardLayout cl = (CardLayout)(getLayout());
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(GrisuCenterPanel.this, LOADING_PANEL);
			}
		});

		getMultiSingleJobMonitoringGrid().displayGridForApplication(application);

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(GrisuCenterPanel.this, SINGLE_JOB_MONITORING_GRID);
			}
		});
	}

	private LoadingPanel getLoadingPanel() {
		if (loadingPanel == null) {
			loadingPanel = new LoadingPanel();
		}
		return loadingPanel;
	}

	private MultiBatchJobMonitoringGrid getMultiBatchJobMonitoringGrid() {
		if (multiBatchJobMonitoringGrid == null) {
			multiBatchJobMonitoringGrid = new MultiBatchJobMonitoringGrid(si);
		}
		return multiBatchJobMonitoringGrid;
	}

	private MultiSingleJobMonitoringGrid getMultiSingleJobMonitoringGrid() {
		if (multiSingleJobMonitoringGrid == null) {
			multiSingleJobMonitoringGrid = new MultiSingleJobMonitoringGrid(si);
		}
		return multiSingleJobMonitoringGrid;
	}


	public void setNavigationCommand(final String[] command) {

		if ( (command == null) || (command.length == 0) ) {
			return;
		}

		if ( GrisuMonitorNavigationTaskPane.SINGLE_JOB_LIST.equals(command[0]) ) {
			displaySingleJobGrid(command[1]);
		} else if ( GrisuMonitorNavigationTaskPaneBatch.BATCH_JOB_LIST.equals(command[0])) {
			displayBatchJobGrid(command[1]);
		}

	}
}
