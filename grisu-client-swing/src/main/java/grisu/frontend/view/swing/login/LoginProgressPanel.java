package grisu.frontend.view.swing.login;

import grisu.control.ServiceInterface;
import grisu.control.events.ClientPropertiesEvent;
import grisu.settings.ClientPropertiesManager;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class LoginProgressPanel extends JPanel implements EventSubscriber {
	private JLabel label;
	private JProgressBar progressBar;
	private JLabel label_1;
	private JCheckBox chckbxAutologinwheneverPossible;

	/**
	 * Create the panel.
	 */
	public LoginProgressPanel() {

		EventBus.subscribe(ClientPropertiesEvent.class, this);

		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("4dlu:grow"),
				ColumnSpec.decode("max(148dlu;default)"),
				ColumnSpec.decode("4dlu:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(14dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getLabel(), "4, 2, center, default");
		add(getProgressBar(), "4, 4, fill, fill");
		add(getMessageLabel(), "4, 6, center, default");
		add(getChckbxAutologinwheneverPossible(), "2, 8, 6, 1");

	}

	private JCheckBox getChckbxAutologinwheneverPossible() {
		if (chckbxAutologinwheneverPossible == null) {
			chckbxAutologinwheneverPossible = new JCheckBox(
					"Always auto-login using existing credential (if available)");

			if (ClientPropertiesManager.getAutoLogin()) {
				chckbxAutologinwheneverPossible.setSelected(true);
			}

			chckbxAutologinwheneverPossible.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					ClientPropertiesManager
							.setAutoLogin(chckbxAutologinwheneverPossible
									.isSelected());

				}
			});
		}
		return chckbxAutologinwheneverPossible;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
		}
		return label;
	}

	private JLabel getMessageLabel() {
		if (label_1 == null) {
			label_1 = new JLabel();
		}
		return label_1;
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

	public void onEvent(Object event) {

		if (event instanceof ClientPropertiesEvent) {
			final ClientPropertiesEvent ev = (ClientPropertiesEvent) event;
			if (ClientPropertiesManager.AUTO_LOGIN_KEY
					.equals(((ClientPropertiesEvent) event).getKey())) {
				try {
					final Boolean b = Boolean.parseBoolean(ev.getValue());
					getChckbxAutologinwheneverPossible().setSelected(b);
				} catch (final Exception e) {
					// not that important
				}
			}
		}

	}

	public void setCreatingServiceInterface() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getProgressBar().setIndeterminate(true);
				getLabel()
						.setText(
								"Logging in...(if this is your first login, it will take a while. Subsequent logins are faster...)");
				getMessageLabel().setText("Using existing local proxy...");
			}
		});

	}

	public void setLoginToBackend(final ServiceInterface si) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				getProgressBar().setIndeterminate(true);

				getLabel()
						.setText(
								"Logging in...(if this is your first login, it will take a while. Subsequent logins are faster...)");
				getMessageLabel().setText(
						"Connecting to: "
								+ ClientPropertiesManager
										.getDefaultServiceInterfaceUrl());
			}

		});

	}
}
