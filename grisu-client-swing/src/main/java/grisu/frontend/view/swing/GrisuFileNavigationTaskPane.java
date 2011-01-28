package grisu.frontend.view.swing;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;
import grisu.model.UserEnvironmentManager;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.jdesktop.swingx.JXTaskPane;

public class GrisuFileNavigationTaskPane extends JXTaskPane {

	public static final String DEFAULT_FILE_MANAGEMENT = "filemanagement";
	public static final String GROUP_FILE_MANAGEMENT = "groupfilemanagement";

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

	public void addDefaultFileManagementPanel() {
		final Action temp = new AbstractAction() {
			{
				putValue(Action.NAME, "File management");
				putValue(Action.SHORT_DESCRIPTION, "File management");
				putValue(Action.SMALL_ICON, FILE_MANAGEMENT_ICON);
			}

			public void actionPerformed(ActionEvent e) {

				navPanel.setNavigationCommand(new String[] { DEFAULT_FILE_MANAGEMENT });

			}
		};

		actions.put(DEFAULT_FILE_MANAGEMENT, temp);
		add(temp);
	}

	public void addGroupFileManagementPanel() {
		final Action temp = new AbstractAction() {
			{
				putValue(Action.NAME, "File management");
				putValue(Action.SHORT_DESCRIPTION, "File management");
				putValue(Action.SMALL_ICON, FILE_MANAGEMENT_ICON);
			}

			public void actionPerformed(ActionEvent e) {

				navPanel.setNavigationCommand(new String[] { GROUP_FILE_MANAGEMENT });

			}
		};

		actions.put(GROUP_FILE_MANAGEMENT, temp);
		add(temp);
	}

	private void initialize() {

	}

}
