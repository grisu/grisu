

package org.vpac.grisu.client.view.swing.filemanager;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserPanel;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GrisuFileCommanderPanel extends JPanel {

	private SiteFileChooserPanel rightSiteFileChooser;
	private SiteFileChooserPanel leftSiteFileChooser;
	private JScrollPane rightScrollPane;
	private JScrollPane leftScrollPane;
	private JButton rightCopyButton;
	private JButton leftCopyButton;
	
	private EnvironmentManager em  = null;

	/**
	 * Create the panel
	 */
	public GrisuFileCommanderPanel(EnvironmentManager em) {
		super();
		this.em = em;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getLeftCopyButton(), new CellConstraints(4, 4));
		add(getRightCopyButton(), new CellConstraints(6, 4));
		add(getLeftScrollPane(), new CellConstraints(2, 2, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		add(getRightScrollPane(), new CellConstraints(6, 2, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		//
		String defaultSite = em.getDefaultSite();
		leftSiteFileChooser.changeToSite(defaultSite);
	}


	protected JButton getLeftCopyButton() {
		if (leftCopyButton == null) {
			leftCopyButton = new JButton();
			leftCopyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Thread() {
						public void run() {
					GrisuFileObject targetDirectory = getRightCurrentDirectory();
					GrisuFileObject[] sourceFiles = getLeftSiteFileChooser()
							.getSelectedFiles();
					try {
						GrisuFileCommanderPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						getLeftSiteFileChooser().setBusy(true);
						getRightSiteFileChooser().setBusy(true);
						em.getFileTransferManager().addTransfer(sourceFiles, targetDirectory, FileTransfer.OVERWRITE_EVERYTHING, true);
//						FileManagerTransferHelpers
//								.transferFiles(em
//										.getServiceInterface(),
//										sourceFiles, targetDirectory, true);
						getRightSiteFileChooser().refreshCurrentDirectory();
					} catch (Exception e1) {
						Utils.showErrorMessage(em, GrisuFileCommanderPanel.this,
								"couldNotTransfer", e1);
					} finally {
						GrisuFileCommanderPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						getLeftSiteFileChooser().setBusy(true);
						getRightSiteFileChooser().setBusy(true);
					}
					
						}
					}.start();
				}
						
			});
			leftCopyButton.setText("copy ->");
		}
		return leftCopyButton;
	}

	protected JButton getRightCopyButton() {
		if (rightCopyButton == null) {
			rightCopyButton = new JButton();
			rightCopyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					new Thread() {
						public void run() {
							GrisuFileObject targetDirectory = getLeftCurrentDirectory();
							GrisuFileObject[] sourceFiles = getRightSiteFileChooser()
									.getSelectedFiles();
							try {
								GrisuFileCommanderPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.WAIT_CURSOR));
								
								em.getFileTransferManager().addTransfer(sourceFiles, targetDirectory, FileTransfer.OVERWRITE_EVERYTHING, true);
//								FileManagerTransferHelpers.transferFiles(
//										em
//												.getServiceInterface(),
//										sourceFiles, targetDirectory, true);
								getLeftSiteFileChooser()
										.refreshCurrentDirectory();
							} catch (Exception e1) {
								Utils.showErrorMessage(em, 
										GrisuFileCommanderPanel.this,
										"couldNotTransfer", e1);
							} finally {
								GrisuFileCommanderPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
						}
					}.start();
				}
			});
			rightCopyButton.setText("<- copy");
		}
		return rightCopyButton;
	}


	protected JScrollPane getLeftScrollPane() {
		if (leftScrollPane == null) {
			leftScrollPane = new JScrollPane();
			leftScrollPane.setViewportView(getLeftSiteFileChooser());
		}
		return leftScrollPane;
	}

	protected JScrollPane getRightScrollPane() {
		if (rightScrollPane == null) {
			rightScrollPane = new JScrollPane();
			rightScrollPane.setViewportView(getRightSiteFileChooser());
		}
		return rightScrollPane;
	}

	protected SiteFileChooserPanel getLeftSiteFileChooser() {
		if (leftSiteFileChooser == null) {
			leftSiteFileChooser = new SiteFileChooserPanel(em);
		}
		return leftSiteFileChooser;
	}

	protected SiteFileChooserPanel getRightSiteFileChooser() {
		if (rightSiteFileChooser == null) {
			rightSiteFileChooser = new SiteFileChooserPanel(em);
			rightSiteFileChooser.changeToSite(FileConstants.LOCAL_NAME);
			File file = new File(System.getProperty("user.home"));
			rightSiteFileChooser.changeCurrentDirectory(em.getFileManager().getFileObject(file.toURI()));

		}
		return rightSiteFileChooser;
	}

	public GrisuFileObject getLeftCurrentDirectory() {
		return getLeftSiteFileChooser().getCurrentDirectory();
	}

	public GrisuFileObject getRightCurrentDirectory() {
		return getRightSiteFileChooser().getCurrentDirectory();
	}

}
