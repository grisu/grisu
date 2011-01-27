package org.vpac.grisu.frontend.view.swing.files.virtual;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vpac.grisu.model.dto.GridFile;

public class DropVirtualGridFileDialog extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DropVirtualGridFileDialog dialog = new DropVirtualGridFileDialog();
			dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String selUrl = null;

	private final DropVirtualGridFilePanel contentPanel = new DropVirtualGridFilePanel();

	/**
	 * Create the dialog.
	 */
	public DropVirtualGridFileDialog() {
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selUrl = null;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			{
				JButton okButton = new JButton("Copy");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selUrl = contentPanel.getSelectedTarget();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	public String getSelectedUrl() {
		return selUrl;
	}

	public void setTargetGridFile(GridFile f) {
		selUrl = null;
		contentPanel.setTargetGridFile(f);
	}

}
