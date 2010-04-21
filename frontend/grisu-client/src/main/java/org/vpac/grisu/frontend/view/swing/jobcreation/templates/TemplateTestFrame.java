package org.vpac.grisu.frontend.view.swing.jobcreation.templates;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.frontend.control.login.LoginManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TemplateTestFrame extends JFrame implements PropertyChangeListener {

	///////////////////////////////////////////////////// inner class ExitAction
	class ExitAction extends AbstractAction {

		//============================================= constructor
		public ExitAction() {
			super("Exit");
			putValue(MNEMONIC_KEY, new Integer('X'));
		}

		//========================================= actionPerformed
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	////////////////////////////////////////////////// inner class OpenAction
	class OpenAction extends AbstractAction {
		//============================================= constructor
		public OpenAction() {
			super("Open...");
			putValue(MNEMONIC_KEY, new Integer('O'));
		}

		//========================================= actionPerformed
		public void actionPerformed(ActionEvent e) {
			int retval = _fileChooser.showOpenDialog(TemplateTestFrame.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File f = _fileChooser.getSelectedFile();
				currentFile = f;
				try {
					FileReader reader = new FileReader(f);
					textArea.read(reader, "");  // Use TextComponent read
				} catch (IOException ioex) {
					System.out.println(e);
					System.exit(1);
				}
			}
		}
	}
	//////////////////////////////////////////////////// inner class SaveAction
	class SaveAction extends AbstractAction {
		//============================================= constructor
		SaveAction() {
			super("Save...");
			putValue(MNEMONIC_KEY, new Integer('S'));
		}

		//========================================= actionPerformed
		public void actionPerformed(ActionEvent e) {
			int retval = _fileChooser.showSaveDialog(TemplateTestFrame.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File f = _fileChooser.getSelectedFile();
				try {
					FileWriter writer = new FileWriter(f);
					textArea.write(writer);  // Use TextComponent write
				} catch (IOException ioex) {
					JOptionPane.showMessageDialog(TemplateTestFrame.this, ioex);
					System.exit(1);
				}
			}
		}
	}
	public static String getStackTrace(Throwable t) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();

		return stringWritter.toString();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					ServiceInterface si = LoginManager.loginCommandline();



					TemplateTestFrame frame = new TemplateTestFrame(si);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected File currentFile;

	private final Action _openAction = new OpenAction();

	private final Action _saveAction = new SaveAction();


	private final Action _exitAction = new ExitAction();

	private final JFileChooser _fileChooser = new JFileChooser();
	private final JPanel contentPane;
	private JSplitPane splitPane;
	private JPanel panel;
	private JPanel panel_1;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	private JButton button;
	private final ServiceInterface si;
	private JPanel errorPanel;
	private JScrollPane scrollPane_1;

	private JTextArea textArea_1;

	private JPanel currentTemplatePanel = null;
	private JButton button_1;
	private JScrollPane scrollPane_2;
	private JTextArea jsdlTextArea;

	private TemplateObject template;

	/**
	 * Create the frame.
	 */
	public TemplateTestFrame(ServiceInterface si) {
		this.si = si;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);

		try {
			contentPane = new JPanel();//
			setContentPane(contentPane);
			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = menuBar.add(new JMenu("File"));
			fileMenu.setMnemonic('F');
			fileMenu.add(_openAction);       // Note use of actions, not text.
			fileMenu.add(_saveAction);
			fileMenu.addSeparator();
			fileMenu.add(_exitAction);
			setJMenuBar(menuBar);
			contentPane.setLayout(new BorderLayout(0, 0));
			contentPane.add(getSplitPane(), BorderLayout.CENTER);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}


	public TemplateObject createTemplatePanel(List<String> lines) throws TemplateException {
		return TemplateHelpers.parseAndCreateTemplatePanel(si, lines);
	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Apply");
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					if ( currentTemplatePanel != null ) {
						getCardPanel().remove(currentTemplatePanel);
					}

					getErrorTextArea().setText("");
					getJsdlTextArea().setText("");
					CardLayout cl = (CardLayout)(getCardPanel().getLayout());
					cl.show(getCardPanel(),"error");

					List<String> lines = new LinkedList(Arrays.asList(getTextArea().getText().split("\n")));

					int size = lines.size();
					try {

						if (  (template != null) && (template.getJobSubmissionObject() != null) ) {
							template.getJobSubmissionObject().removePropertyChangeListener(TemplateTestFrame.this);
						}
						template = createTemplatePanel(lines);
						template.getJobSubmissionObject().addPropertyChangeListener(TemplateTestFrame.this);

						currentTemplatePanel = template.getTemplatePanel();

						try {
							getJsdlTextArea().setText(template.getJobSubmissionObject().getJobDescriptionDocumentAsString());
						} catch (JobPropertiesException e) {
							StringBuffer temp = new StringBuffer("Can't calculate jsdl right now: "+e.getLocalizedMessage()+"\n\n");
							temp.append(getStackTrace(e));
							getJsdlTextArea().setText(temp.toString());
							getJsdlTextArea().setCaretPosition(0);
						}

						getCardPanel().add(currentTemplatePanel, "currentTemplate");
						cl.show(getCardPanel(),"currentTemplate");


					} catch (TemplateException e) {

						StringBuffer temp = new StringBuffer("Error when building template: "+e.getLocalizedMessage()+"\n\n");
						temp.append(getStackTrace(e));
						getErrorTextArea().setText(temp.toString());
						cl.show(getCardPanel(),"error");
					}

				}
			});
		}
		return button;
	}

	private JButton getButton_1() {
		if (button_1 == null) {
			button_1 = new JButton("Save");
			button_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					File f = currentFile;
					if ( f == null ) {
						int retval = _fileChooser.showSaveDialog(TemplateTestFrame.this);
						if (retval == JFileChooser.APPROVE_OPTION) {
							f = _fileChooser.getSelectedFile();
						} else {
							return;
						}
					}
					try {
						FileWriter writer = new FileWriter(f);
						textArea.write(writer);  // Use TextComponent write
					} catch (IOException ioex) {
						JOptionPane.showMessageDialog(TemplateTestFrame.this, ioex);
						System.exit(1);
					}
				}
			});
		}
		return button_1;
	}
	private JPanel getCardPanel() {
		if ( panel == null ) {
			panel = new JPanel();
			panel.setLayout(new CardLayout(0, 0));
			panel.add(getErrorPanel(), "error");
		}
		return panel;
	}

	private JPanel getErrorPanel() {
		if (errorPanel == null) {
			errorPanel = new JPanel();
			errorPanel.setLayout(new BorderLayout(0, 0));
			errorPanel.add(getScrollPane_1(), BorderLayout.CENTER);
		}
		return errorPanel;
	}

	private JTextArea getErrorTextArea() {
		if (textArea_1 == null) {
			textArea_1 = new JTextArea();
			textArea_1.setEditable(false);
		}
		return textArea_1;
	}
	private JTextArea getJsdlTextArea() {
		if (jsdlTextArea == null) {
			jsdlTextArea = new JTextArea();
			jsdlTextArea.setEditable(false);
		}
		return jsdlTextArea;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			panel_1.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,},
					new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow"),
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("max(79dlu;default)"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,}));
			panel_1.add(getScrollPane(), "2, 2, 3, 1, fill, fill");
			panel_1.add(getScrollPane_2(), "2, 4, 3, 1, fill, fill");
			panel_1.add(getButton(), "2, 6, right, default");
			panel_1.add(getButton_1(), "4, 6, right, default");
		}
		return panel_1;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	private JScrollPane getScrollPane_1() {
		if (scrollPane_1 == null) {
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getErrorTextArea());
		}
		return scrollPane_1;
	}
	private JScrollPane getScrollPane_2() {
		if (scrollPane_2 == null) {
			scrollPane_2 = new JScrollPane();
			scrollPane_2.setViewportView(getJsdlTextArea());
		}
		return scrollPane_2;
	}
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getCardPanel());
			splitPane.setRightComponent(getPanel_1());
			splitPane.setDividerLocation(400);
		}
		return splitPane;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
		}
		return textArea;
	}

	public void propertyChange(PropertyChangeEvent arg0) {

		if ( (template != null) && (template.getJobSubmissionObject() != null) ) {
			try {
				getJsdlTextArea().setText(template.getJobSubmissionObject().getJobDescriptionDocumentAsString());
			} catch (JobPropertiesException e) {
				StringBuffer temp = new StringBuffer("Can't calculate jsdl right now: "+e.getLocalizedMessage()+"\n\n");
				temp.append(getStackTrace(e));
				getJsdlTextArea().setText(temp.toString());
				getJsdlTextArea().setCaretPosition(0);
			}
		}

	}
}
