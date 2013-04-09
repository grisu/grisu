package grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class SingleInputGridFile extends AbstractInputGridFile {

	private JButton btnBrowse;

	/**
	 * Create the panel.
	 */
	public SingleInputGridFile() {
		super();

		setBorder(new TitledBorder(null, "Input file", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getInputFileComboBox(), "2, 2, fill, default");
		add(getBtnBrowse(), "4, 2");

	}

	protected JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					askForFile();
				}
			});
		}
		return btnBrowse;
	}

}
