package grisu.frontend.view.swing.jobcreation.widgets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class TextField extends AbstractWidget {
	private JTextField textField;

	public TextField() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		add(getTextField(), "2, 2, fill, default");
	}

	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
			textField.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent keyevent) {

				}

				public void keyReleased(KeyEvent keyevent) {
					getPropertyChangeSupport().firePropertyChange(getTitle(),
							null, getValue());
				}

				public void keyTyped(KeyEvent keyevent) {
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
		getTextField().setText(value);
	}
}
