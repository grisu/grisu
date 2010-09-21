package org.vpac.grisu.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.jdesktop.swingx.JXTaskPane;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;

public class GrisuFileNavigationTaskPane extends JXTaskPane {

	public static final String FILE_MANAGEMENT = "filemanagement";

	public static final Icon FILE_MANAGEMENT_ICON = null;

	private final ServiceInterface si;

	private final UserEnvironmentManager em;
	private final GrisuNavigationPanel navPanel;

	private final Map<String, Action> actions = new HashMap<String, Action>();

	/**
	 * Create the panel.
	 */
	public GrisuFileNavigationTaskPane(ServiceInterface si,
			GrisuNavigationPanel navPanel) {

		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.navPanel = navPanel;

		setTitle("Files");
		initialize();
	}

	private void initialize() {

		final Action temp = new AbstractAction() {
			{
				putValue(Action.NAME, "File management");
				putValue(Action.SHORT_DESCRIPTION, "File management");
				putValue(Action.SMALL_ICON, FILE_MANAGEMENT_ICON);
			}

			public void actionPerformed(ActionEvent e) {

				navPanel.setNavigationCommand(new String[] { FILE_MANAGEMENT });

			}
		};

		actions.put(FILE_MANAGEMENT, temp);
		add(temp);
	}

}
