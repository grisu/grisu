package org.vpac.grisu.frontend.view.swing.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.TemplateManager;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ApplicationSubscribePanel extends JPanel {

	static final Logger myLogger = Logger
			.getLogger(ApplicationSubscribePanel.class.getName());

	private JLabel lblRemoteApplications;
	private JScrollPane scrollPane;
	private JLabel lblMyApplications;
	private JButton button;
	private JButton button_1;
	private JList remoteApplicationList;
	private JScrollPane scrollPane_1;
	private JLabel lblAddLocalApplication;
	private JButton btnBrowse;
	private JScrollPane scrollPane_2;
	private JLabel lblAdd;
	private JLabel lblRemove;
	private JButton btnRemove;
	private JList myRemoveApplicationList;
	private JList localApplicationList;
	private JSeparator separator;
	private JSeparator separator_1;

	private final DefaultListModel remoteModel = new DefaultListModel();
	private final DefaultListModel myRemoteModel = new DefaultListModel();
	private final DefaultListModel localModel = new DefaultListModel();

	private ServiceInterface si;

	private TemplateManager tm;

	/**
	 * Create the panel.
	 */
	public ApplicationSubscribePanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(25dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(20dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(82dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(34dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(41dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLabel_1(), "8, 2");
		add(getSeparator_1(), "2, 4, 7, 1");
		add(getLblRemoteApplications(), "2, 6, 3, 1");
		add(getScrollPane(), "2, 8, 3, 3, fill, fill");
		add(getButton(), "6, 8, default, bottom");
		add(getScrollPane_1(), "8, 8, 1, 3, fill, fill");
		add(getButton_1(), "6, 10, default, top");
		add(getSeparator(), "2, 12, 7, 1");
		add(getLblAddLocalApplication(), "2, 14, 7, 1");
		add(getLblAdd(), "2, 16, right, center");
		add(getBtnBrowse(), "4, 16, right, top");
		add(getScrollPane_2(), "8, 16, 1, 3, fill, fill");
		add(getLblRemove(), "2, 18, right, default");
		add(getBtnRemove(), "4, 18, right, default");

	}

	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					final JFileChooser fc = new JFileChooser();
					fc.setFileFilter(new GrisuTemplateFileFilter());
					int returnVal = fc
							.showOpenDialog(ApplicationSubscribePanel.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();

						String templateName = tm.addLocalTemplate(file);
						localModel.addElement(templateName);
					}
				}
			});
		}
		return btnBrowse;
	}

	private JButton getBtnRemove() {
		if (btnRemove == null) {
			btnRemove = new JButton("Remove");
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					for (Object o : getLocalList().getSelectedValues()) {
						String name = (String) o;
						tm.removeLocalApplication(name);
						localModel.removeElement(name);
					}

				}
			});
		}
		return btnRemove;
	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("->");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					for (Object o : getRemoteApplicationList()
							.getSelectedValues()) {
						String name = (String) o;
						try {
							tm.addRemoteTemplate(name);
						} catch (NoSuchTemplateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						myRemoteModel.addElement(name);
						remoteModel.removeElement(name);
					}

				}
			});
		}
		return button;
	}

	private JButton getButton_1() {
		if (button_1 == null) {
			button_1 = new JButton("<-");
			button_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					for (Object o : getMyRemoveApplicationList()
							.getSelectedValues()) {
						String name = (String) o;
						tm.removeRemoteApplication(name);
						myRemoteModel.removeElement(name);
						remoteModel.addElement(name);
					}

				}
			});
		}
		return button_1;
	}

	private JLabel getLabel_1() {
		if (lblMyApplications == null) {
			lblMyApplications = new JLabel("My applications");
		}
		return lblMyApplications;
	}

	private JLabel getLblAdd() {
		if (lblAdd == null) {
			lblAdd = new JLabel("Add");
		}
		return lblAdd;
	}

	private JLabel getLblAddLocalApplication() {
		if (lblAddLocalApplication == null) {
			lblAddLocalApplication = new JLabel("Local applications");
		}
		return lblAddLocalApplication;
	}

	private JLabel getLblRemoteApplications() {
		if (lblRemoteApplications == null) {
			lblRemoteApplications = new JLabel("Remote applications");
		}
		return lblRemoteApplications;
	}

	private JLabel getLblRemove() {
		if (lblRemove == null) {
			lblRemove = new JLabel("Remove");
		}
		return lblRemove;
	}

	private JList getLocalList() {
		if (localApplicationList == null) {
			localApplicationList = new JList(localModel);
		}
		return localApplicationList;
	}

	private JList getMyRemoveApplicationList() {
		if (myRemoveApplicationList == null) {
			myRemoveApplicationList = new JList(myRemoteModel);
		}
		return myRemoveApplicationList;
	}

	private JList getRemoteApplicationList() {
		if (remoteApplicationList == null) {
			remoteApplicationList = new JList(remoteModel);
		}
		return remoteApplicationList;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getRemoteApplicationList());
		}
		return scrollPane;
	}

	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getMyRemoveApplicationList());
		}
		return scrollPane_1;
	}

	private JScrollPane getScrollPane_2() {
		if (scrollPane_2 == null) {
			scrollPane_2 = new JScrollPane();
			scrollPane_2.setViewportView(getLocalList());
		}
		return scrollPane_2;
	}

	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
		}
		return separator;
	}

	private JSeparator getSeparator_1() {
		if (separator_1 == null) {
			separator_1 = new JSeparator();
		}
		return separator_1;
	}

	public void setServiceInterface(ServiceInterface si) {

		this.si = si;
		this.tm = GrisuRegistryManager.getDefault(si).getTemplateManager();

		remoteModel.clear();
		localModel.clear();
		myRemoteModel.clear();

		// my remote list
		for (String name : ClientPropertiesManager.getServerTemplates()) {

			try {
				List<String> rt = tm.getRemoteTemplate(name);
			} catch (NoSuchTemplateException e) {
				myLogger.info("Could not find template " + name
						+ " on this backend. Ignoring it...");
				continue;
			}
			myRemoteModel.addElement(name);
		}

		// remote list
		for (String name : tm.getRemoteTemplateNames()) {
			if (!myRemoteModel.contains(name)) {
				remoteModel.addElement(name);
			}
		}
		// local list
		for (String name : tm.getLocalTemplates().keySet()) {
			localModel.addElement(name);
		}

	}
}
