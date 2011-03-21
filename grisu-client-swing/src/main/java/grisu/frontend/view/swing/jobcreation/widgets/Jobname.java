package grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Jobname extends AbstractWidget {

	public static final String REPLACEMENT_CHARACTERS = "\\s|;|'|\"|,|\\$|\\?|#";

	public static String JOBNAME_CALC_METHOD_KEY = "jobnameCalcMethod";


	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public Jobname() {
		super();
		setBorder(new TitledBorder(null, "Jobname", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getTextField(), "2, 2, fill, default");
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
			textField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					String input = textField.getText();
					final int index = textField.getCaretPosition();
					input = input.replaceAll(REPLACEMENT_CHARACTERS, "_");
					textField.setText(input.trim());
					textField.setCaretPosition(index);
				}

			});

		}
		return textField;
	}

	@Override
	public String getValue() {
		return getTextField().getText();
	}

	@Override
	public void setValue(String value) {

		getTextField().setText(value.replaceAll(REPLACEMENT_CHARACTERS, "_"));

	}
}
