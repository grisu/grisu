package org.vpac.grisu.frontend.view.swing;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.preview.FileListWithPreviewPanel;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.batch.MultiBatchJobMonitoringGrid;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.single.MultiSingleJobMonitoringGrid;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GrisuCenterPanel extends JPanel {

	public static final String LOADING_PANEL = "loading";
	public static final String SINGLE_JOB_MONITORING_GRID = "singleJobMontitoring";
	public static final String BATCH_JOB_MONITORING_GRID = "batchJobMonitoring";
	public static final String FILE_MANAGEMENT_PANEL = "fileManagement";

	private MultiSingleJobMonitoringGrid multiSingleJobMonitoringGrid;
	private MultiBatchJobMonitoringGrid multiBatchJobMonitoringGrid;

	private final ServiceInterface si;
	private LoadingPanel loadingPanel;
	private FileListWithPreviewPanel fileListWithPreviewPanel;

	private final JPanel wrapperPanel;

	/**
	 * Create the panel.
	 */
	public GrisuCenterPanel(ServiceInterface si) {
		this.si = si;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("219px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("154px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));

		wrapperPanel = new JPanel();

		wrapperPanel.setLayout(new CardLayout(0, 0));
		wrapperPanel.add(getLoadingPanel(), LOADING_PANEL);
		wrapperPanel.add(getMultiSingleJobMonitoringGrid(), SINGLE_JOB_MONITORING_GRID);
		wrapperPanel.add(getMultiBatchJobMonitoringGrid(), BATCH_JOB_MONITORING_GRID);
		wrapperPanel.add(getFileListWithPreviewPanel(), FILE_MANAGEMENT_PANEL);

		add(wrapperPanel, "2, 2, fill, fill");
	}

	public void displayBatchJobGrid(String application) {

		final CardLayout cl = (CardLayout)(wrapperPanel.getLayout());

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				loadingPanel.setLoading(true);
				cl.show(wrapperPanel, LOADING_PANEL);
			}
		});

		getMultiBatchJobMonitoringGrid().displayGridForApplication(application);

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(wrapperPanel, BATCH_JOB_MONITORING_GRID);
				loadingPanel.setLoading(false);
			}
		});
	}

	public void displayFileManagement() {

		final CardLayout cl = (CardLayout)(wrapperPanel.getLayout());
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(wrapperPanel, FILE_MANAGEMENT_PANEL);
				revalidate();
			}
		});
	}

	public void displaySingleJobGrid(String application) {

		final CardLayout cl = (CardLayout)(wrapperPanel.getLayout());

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				loadingPanel.setLoading(true);
				cl.show(wrapperPanel, LOADING_PANEL);
			}
		});

		getMultiSingleJobMonitoringGrid().displayGridForApplication(application);

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				cl.show(wrapperPanel, SINGLE_JOB_MONITORING_GRID);
				loadingPanel.setLoading(false);
			}
		});
	}

	private FileListWithPreviewPanel getFileListWithPreviewPanel() {
		if (fileListWithPreviewPanel == null) {
			fileListWithPreviewPanel = new FileListWithPreviewPanel(si, null, ClientPropertiesManager.getLastUsedLeftUrl(),
					true, true, true, true, true);

		}
		return fileListWithPreviewPanel;
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
		} else if ( GrisuFileNavigationTaskPane.FILE_MANAGEMENT.equals(command[0]) ) {
			displayFileManagement();
		}

	}
}
