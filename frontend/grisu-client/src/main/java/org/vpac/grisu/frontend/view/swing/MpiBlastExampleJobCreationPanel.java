package org.vpac.grisu.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.files.GrisuFileDialog;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.files.GlazedFile;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MpiBlastExampleJobCreationPanel extends JPanel implements
		JobCreationPanel {

	static final Logger myLogger = Logger
			.getLogger(MpiBlastExampleJobCreationPanel.class.getName());

	private ServiceInterface si;
	private JLabel jobnameLabel;
	private JTextField jobnameField;
	private JLabel lblProgram;
	private JComboBox programCombobox;
	private JLabel lblDatabase;
	private JComboBox databaseComboBox;
	private JLabel lblFastaFile;
	private JComboBox inputFileComboBox;
	private JButton btnBrowse;
	private JLabel lblOfJobs;
	private JSlider slider;

	private GrisuFileDialog dialog;

	private GlazedFile currentFile;
	private List<String> currentFastaInput;
	private FileManager fm;

	/**
	 * Create the panel.
	 */
	public MpiBlastExampleJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
		add(getJobnameLabel(), "2, 2, right, default");
		add(getJobnameField(), "4, 2, 5, 1, fill, default");
		add(getLblProgram(), "2, 4, right, default");
		add(getProgramCombobox(), "4, 4, fill, default");
		add(getLblDatabase(), "6, 4, right, default");
		add(getDatabaseComboBox(), "8, 4, fill, default");
		add(getLblFastaFile(), "2, 6, right, default");
		add(getInputFileComboBox(), "4, 6, 3, 1, fill, default");
		add(getBtnBrowse(), "8, 6");
		add(getLblOfJobs(), "2, 10, right, default");
		add(getSlider(), "4, 10, 5, 1");

	}

	public boolean createsBatchJob() {
		return true;
	}

	public boolean createsSingleJob() {
		return false;
	}

	private JButton getBtnBrowse() {
		if (btnBrowse == null) {
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if (si == null) {
						myLogger.error("ServiceInterface not set yet.");
						return;
					}

					GlazedFile file = popupFileDialogAndAskForFile();

					if (file == null) {
						return;
					}

					setInputFile(file);

				}
			});
		}
		return btnBrowse;
	}

	private JComboBox getDatabaseComboBox() {
		if (databaseComboBox == null) {
			databaseComboBox = new JComboBox();
			databaseComboBox.setModel(new DefaultComboBoxModel(new String[] {
					"nt", "nr" }));
		}
		return databaseComboBox;
	}

	public GrisuFileDialog getFileDialog() {

		if (si == null) {
			myLogger.error("Serviceinterface not set yet...");
			return null;
		}

		if (dialog == null) {
			dialog = new GrisuFileDialog(si, System.getProperty("user.home"));
		}
		return dialog;
	}

	private JComboBox getInputFileComboBox() {
		if (inputFileComboBox == null) {
			inputFileComboBox = new JComboBox();
		}
		return inputFileComboBox;
	}

	private JTextField getJobnameField() {
		if (jobnameField == null) {
			jobnameField = new JTextField();
			jobnameField.setColumns(10);
		}
		return jobnameField;
	}

	private JLabel getJobnameLabel() {
		if (jobnameLabel == null) {
			jobnameLabel = new JLabel("Jobname:");
		}
		return jobnameLabel;
	}

	private JLabel getLblDatabase() {
		if (lblDatabase == null) {
			lblDatabase = new JLabel("Database:");
		}
		return lblDatabase;
	}

	private JLabel getLblFastaFile() {
		if (lblFastaFile == null) {
			lblFastaFile = new JLabel("Fasta file:");
		}
		return lblFastaFile;
	}

	private JLabel getLblOfJobs() {
		if (lblOfJobs == null) {
			lblOfJobs = new JLabel("# of jobs");
		}
		return lblOfJobs;
	}

	private JLabel getLblProgram() {
		if (lblProgram == null) {
			lblProgram = new JLabel("Program:");
		}
		return lblProgram;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getPanelName() {
		return "MPIBlast example";
	}

	private JComboBox getProgramCombobox() {
		if (programCombobox == null) {
			programCombobox = new JComboBox();
			programCombobox.setModel(new DefaultComboBoxModel(new String[] {
					"blastn", "blastp", "blastx", "tblastn", "tblastx" }));
		}
		return programCombobox;
	}

	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider();
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setEnabled(false);
			slider.setMinimum(1);
			slider.setMaximum(1);
		}
		return slider;
	}

	public String getSupportedApplication() {
		return "mpiBlast";
	}

	private void parseFastaFile() {

		try {
			currentFastaInput = FileUtils.readLines(fm
					.getLocalCacheFile(currentFile.getUrl()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected GlazedFile popupFileDialogAndAskForFile() {

		getFileDialog().setVisible(true);

		GlazedFile file = getFileDialog().getSelectedFile();
		getFileDialog().clearSelection();

		GlazedFile currentDir = getFileDialog().getCurrentDirectory();

		GrisuRegistryManager.getDefault(si).getHistoryManager()
				.addHistoryEntry("MPIBLAST_EXAMPLE_LAST_INPUT_FILE_DIR",
						currentDir.getUrl());

		return file;
	}

	private void setInputFile(GlazedFile file) {

		currentFile = file;
		getInputFileComboBox().setSelectedItem(file.getUrl());

		if (currentFile == null) {
			return;
		}

		getSlider().setEnabled(true);

		parseFastaFile();

		getSlider().setMaximum(currentFastaInput.size());

	}

	public void setServiceInterface(ServiceInterface si) {

		this.si = si;
		this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
	}
}
