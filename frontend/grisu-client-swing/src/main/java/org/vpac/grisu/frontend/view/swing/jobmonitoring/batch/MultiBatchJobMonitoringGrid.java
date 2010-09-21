package org.vpac.grisu.frontend.view.swing.jobmonitoring.batch;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

public class MultiBatchJobMonitoringGrid extends JPanel {

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private final Map<String, BatchJobTabbedPane> grids = new HashMap<String, BatchJobTabbedPane>();

	/**
	 * Create the panel.
	 */
	public MultiBatchJobMonitoringGrid(ServiceInterface si) {

		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();

		setLayout(new CardLayout(0, 0));
	}

	public void displayGridForApplication(final String application) {

		if (grids.get(application) == null) {

			final BatchJobTabbedPane temp = new BatchJobTabbedPane(si,
					application);
			grids.put(application, temp);
			add(temp, application);
		}

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				final CardLayout cl = (CardLayout) (getLayout());
				cl.show(MultiBatchJobMonitoringGrid.this, application);
			}

		});

	}

	public SortedSet<String> getAvailableApplications(boolean refreshOnBackend) {
		return em.getCurrentApplicationsBatch(refreshOnBackend);
	}

}
