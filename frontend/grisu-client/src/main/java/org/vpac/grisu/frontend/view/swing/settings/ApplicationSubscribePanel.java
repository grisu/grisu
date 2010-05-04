package org.vpac.grisu.frontend.view.swing.settings;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.TemplateManager;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateEditDialog;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.Environment;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ApplicationSubscribePanel extends JPanel {

	static final Logger myLogger = Logger
			.getLogger(ApplicationSubscribePanel.class.getName());

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

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
	private JButton remoteToLocalButton;

	private JPopupMenu popupMenu;
	private JMenuItem editItem;

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
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLabel_1(), "8, 2");
		add(getSeparator_1(), "2, 4, 7, 1");
		add(getLblRemoteApplications(), "2, 6, 3, 1");
		add(getScrollPane(), "2, 8, 3, 5, fill, fill");
		add(getButton(), "6, 8, default, bottom");
		add(getScrollPane_1(), "8, 8, 1, 3, fill, fill");
		add(getButton_1(), "6, 10, 1, 3, default, top");
		add(getRemoteToLocalButton(), "8, 12, center, default");
		add(getSeparator(), "2, 14, 7, 1");
		add(getLblAddLocalApplication(), "2, 16, 7, 1");
		add(getBtnBrowse(), "4, 18, fill, bottom");
		add(getScrollPane_2(), "8, 18, 1, 3, fill, fill");
		add(getBtnRemove(), "4, 20, right, top");

	}

	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Add");
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

	private JMenuItem getEditItem() {
		if (editItem == null) {
			editItem = new JMenuItem("Edit");
			editItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					for (Object name : getLocalList().getSelectedValues()) {

						File templateFile = new File(Environment
								.getTemplateDirectory(), (String) name
								+ ".template");
						try {
							TemplateEditDialog dialog = new TemplateEditDialog(
									si, templateFile);
							dialog.setVisible(true);
						} catch (TemplateException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}
			});
		}
		return editItem;
	}

	private JLabel getLabel_1() {
		if (lblMyApplications == null) {
			lblMyApplications = new JLabel("My applications");
		}
		return lblMyApplications;
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

	private JList getLocalList() {
		if (localApplicationList == null) {
			localApplicationList = new JList(localModel);
			addPopup(localApplicationList, getPopupMenu());
		}
		return localApplicationList;
	}

	private JList getMyRemoveApplicationList() {
		if (myRemoveApplicationList == null) {
			myRemoveApplicationList = new JList(myRemoteModel);
		}
		return myRemoveApplicationList;
	}

	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getEditItem());
		}
		return popupMenu;
	}

	private JList getRemoteApplicationList() {
		if (remoteApplicationList == null) {
			remoteApplicationList = new JList(remoteModel);
		}
		return remoteApplicationList;
	}

	private JButton getRemoteToLocalButton() {
		if (remoteToLocalButton == null) {
			remoteToLocalButton = new JButton("V");
			remoteToLocalButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					for (Object o : getMyRemoveApplicationList()
							.getSelectedValues()) {
						String name = (String) o;
						try {
							String newName = tm
									.copyTemplateToLocalTemplateStore(name);
							localModel.addElement(newName);
						} catch (NoSuchTemplateException e) {
							e.printStackTrace();
						}
					}

				}
			});
		}
		return remoteToLocalButton;
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
