package grisu.frontend.view.swing.utils.ssh;

import grisu.control.ServiceInterface;
import grisu.jcommons.configuration.CommonGridProperties;
import grisu.model.FileManager;
import grisu.model.MountPoint;
import grith.jgrith.utils.GridSshKey;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SshKeyCopyPanel extends JPanel {
	private JScrollPane scrollPane;
	private JList list;

	private final DefaultListModel mountpointModel = new DefaultListModel();
	private JButton btnEnableSsh;

	private ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public SshKeyCopyPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getScrollPane(), "2, 2, fill, fill");
		add(getBtnEnableSsh(), "2, 4, right, default");

	}

	private JButton getBtnEnableSsh() {
		if (btnEnableSsh == null) {
			btnEnableSsh = new JButton("Enable ssh");
			btnEnableSsh.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					lockUI(true);
					final Object[] mps = getList().getSelectedValues();

					Thread t = new Thread() {
						@Override
						public void run() {

							try {
								if (!GridSshKey.defaultGridsshkeyExists()) {
									GridSshKey.getDefaultGridsshkey("myPassword".toCharArray(),
											"testid");
								}

								FileManager fm = new FileManager(si);

								for (Object mpo : mps) {
									MountPoint mp = (MountPoint) mpo;

									String target = mp.getRootUrl()
											+ "/testfolderforsshcert/"
											+ CommonGridProperties.KEY_NAME+CommonGridProperties.CERT_EXTENSION;

									System.out
									.println("Copying key from "
											+ CommonGridProperties.getDefault()
											.getGridSSHCert() + " to "
											+ target);

									fm.uploadFile(new File(CommonGridProperties
											.getDefault().getGridSSHCert().toString()),
											target,
											true);

									System.out.println("Copying finished.");

								}


							} catch (Exception e) {
								System.out.println("Error: "
										+ e.getLocalizedMessage());
								e.printStackTrace();
							} finally {
								lockUI(false);
							}
						}
					};
					t.start();

				}
			});
		}
		return btnEnableSsh;
	}

	private JList getList() {
		if (list == null) {
			list = new JList(mountpointModel);
		}
		return list;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}

	private void lockUI(final boolean lock){

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				getList().setEnabled(!lock);
				getBtnEnableSsh().setEnabled(!lock);
			}

		});


	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;

		final List<MountPoint> mps = si.df().getMountpoints();

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				for (MountPoint mp : mps) {
					mountpointModel.addElement(mp);
				}
			}
		});

	}
}
