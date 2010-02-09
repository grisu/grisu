package org.vpac.grisu.frontend.view.swing.login;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LoginProgressPanel extends JPanel {
	private JLabel label;
	private JProgressBar progressBar;
	private JLabel label_1;
	private JCheckBox chckbxAutologinwheneverPossible;

	/**
	 * Create the panel.
	 */
	public LoginProgressPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("4dlu:grow"),
				ColumnSpec.decode("max(148dlu;default)"),
				ColumnSpec.decode("4dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(14dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLabel(), "4, 2, center, default");
		add(getProgressBar(), "4, 4, fill, fill");
		add(getMessageLabel(), "4, 6, center, default");
		add(getChckbxAutologinwheneverPossible(), "2, 8, 6, 1");

	}

	private JCheckBox getChckbxAutologinwheneverPossible() {
		if (chckbxAutologinwheneverPossible == null) {
			chckbxAutologinwheneverPossible = new JCheckBox("Auto-login (whenever possible)");

			if ( ClientPropertiesManager.getAutoLogin() ) {
				chckbxAutologinwheneverPossible.setSelected(true);
			}

			chckbxAutologinwheneverPossible.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					ClientPropertiesManager.setAutoLogin(chckbxAutologinwheneverPossible.isSelected());

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

	public void setCreatingServiceInterface() {

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getProgressBar().setIndeterminate(true);
				getLabel().setText("Logging in...");
				getMessageLabel().setText("Using existing local proxy...");
			}
		});

	}

	public void setLoginToBackend(final ServiceInterface si) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				getProgressBar().setIndeterminate(true);

				getLabel().setText("Logging in...");
				getMessageLabel().setText("Connecting to: "+ClientPropertiesManager.getDefaultServiceInterfaceUrl());
			}

		});

	}
}
