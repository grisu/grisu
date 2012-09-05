package grisu.frontend.view.swing.jobcreation.createJobPanels;

import javax.swing.JPanel;

public class MpiBlastExampleJobCreationPanel extends JPanel {

	// static final Logger myLogger = LoggerFactory
	// .getLogger(MpiBlastExampleJobCreationPanel.class.getName());
	//
	// public static final String MPIBLAST_EXAMPLE_LAST_INPUT_FILE_DIR =
	// "mpiblast_batch_last_input_file_dir";
	// public static final String MPIBLAST_BATCH_INPUT_FILES =
	// "mpiblast_batch_input_files";
	//
	// static final int DEFAULT_WALLTIME = 3600 * 24;
	//
	// private ServiceInterface si;
	// private JLabel jobnameLabel;
	// private JTextField jobnameField;
	// private JLabel lblProgram;
	// private JComboBox programCombobox;
	// private JLabel lblDatabase;
	// private JComboBox databaseComboBox;
	// private JLabel lblFastaFile;
	// private JComboBox inputFileComboBox;
	// private JButton btnBrowse;
	// private JLabel lblOfJobs;
	// private JSlider slider;
	//
	// private BatchJobObject currentBatchJob;
	//
	// private GrisuFileDialog dialog;
	//
	// private GlazedFile currentFile;
	// private List<List<String>> currentParsedFastaInput;
	// private FileManager fm;
	// private UserEnvironmentManager uem;
	// private HistoryManager hm;
	// private JLabel noJobsLabel;
	// private JButton submitButton;
	// private JobSubmissionLogPanel jobSubmissionLogPanel;
	// private Thread subThread;
	//
	// private static final NumberFormat formatter = new DecimalFormat("0000");
	//
	// /**
	// * Create the panel.
	// */
	// public MpiBlastExampleJobCreationPanel() {
	// setLayout(new FormLayout(new ColumnSpec[] {
	// FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
	// FormFactory.RELATED_GAP_COLSPEC,
	// ColumnSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
	// FormFactory.RELATED_GAP_COLSPEC,
	// ColumnSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_COLSPEC,
	// ColumnSpec.decode("max(16dlu;default)"),
	// FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC,
	// RowSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_ROWSPEC, }));
	// add(getLblFastaFile(), "2, 2, right, default");
	// add(getInputFileComboBox(), "4, 2, 5, 1, fill, default");
	// add(getBtnBrowse(), "10, 2");
	// add(getJobnameLabel(), "2, 4, right, default");
	// add(getJobnameField(), "4, 4, 7, 1, fill, default");
	// add(getLblProgram(), "2, 6, right, default");
	// add(getProgramCombobox(), "4, 6, fill, default");
	// add(getLblDatabase(), "6, 6, right, default");
	// add(getDatabaseComboBox(), "8, 6, 3, 1, fill, default");
	// add(getLblOfJobs(), "2, 10, right, default");
	// add(getSlider(), "4, 10, 5, 1");
	// add(getNoJobsLabel(), "10, 10, center, default");
	// add(getSubmitButton(), "10, 12");
	// add(getJobSubmissionLogPanel(), "2, 14, 9, 1, fill, fill");
	//
	// }
	//
	// private void cleanUpUI() {
	//
	// getInputFileComboBox().removeAllItems();
	// for (final String entry : hm.getEntries(MPIBLAST_BATCH_INPUT_FILES)) {
	// getInputFileComboBox().addItem(entry);
	// }
	// getInputFileComboBox().setSelectedItem(null);
	// getSlider().setEnabled(false);
	// getSlider().setMaximum(1);
	// getNoJobsLabel().setText("n/a");
	// getSubmitButton().setText("Submit");
	// getJobSubmissionLogPanel().clear();
	// getJobnameField().setText("");
	// currentParsedFastaInput = null;
	// currentFile = null;
	//
	// }
	//
	// public boolean createsBatchJob() {
	// return true;
	// }
	//
	// public boolean createsSingleJob() {
	// return false;
	// }
	//
	// private JButton getBtnBrowse() {
	// if (btnBrowse == null) {
	// btnBrowse = new JButton("Browse");
	// btnBrowse.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent arg0) {
	//
	// if (si == null) {
	// myLogger.error("ServiceInterface not set yet.");
	// return;
	// }
	//
	// final GlazedFile file = popupFileDialogAndAskForFile();
	//
	// if (file == null) {
	// return;
	// }
	//
	// setInputFile(file);
	//
	// }
	// });
	// }
	// return btnBrowse;
	// }
	//
	// private JComboBox getDatabaseComboBox() {
	// if (databaseComboBox == null) {
	// databaseComboBox = new JComboBox();
	// databaseComboBox.setModel(new DefaultComboBoxModel(new String[] {
	// "nt", "nr" }));
	// }
	// return databaseComboBox;
	// }
	//
	// public GrisuFileDialog getFileDialog() {
	//
	// if (si == null) {
	// myLogger.error("Serviceinterface not set yet...");
	// return null;
	// }
	//
	// if (dialog == null) {
	// String url = hm.getLastEntry(MPIBLAST_EXAMPLE_LAST_INPUT_FILE_DIR);
	// if (StringUtils.isBlank(url)) {
	// url = new File(System.getProperty("user.home")).toURI()
	// .toString();
	// }
	// dialog = new GrisuFileDialog(
	// SwingUtilities.getWindowAncestor(this), si, url);
	// }
	// return dialog;
	// }
	//
	// private JComboBox getInputFileComboBox() {
	// if (inputFileComboBox == null) {
	// inputFileComboBox = new JComboBox();
	// inputFileComboBox.setEditable(true);
	// inputFileComboBox.addItemListener(new ItemListener() {
	//
	// public void itemStateChanged(ItemEvent e) {
	//
	// if (ItemEvent.SELECTED == e.getStateChange()) {
	//
	// try {
	// final GlazedFile file = fm
	// .createGlazedFileFromUrl((String) inputFileComboBox
	// .getSelectedItem());
	//
	// setInputFile(file);
	// } catch (final Exception e1) {
	//
	// JXErrorPane.showDialog(e1);
	//
	// }
	//
	// }
	//
	// }
	// });
	// }
	// return inputFileComboBox;
	// }
	//
	// private JTextField getJobnameField() {
	// if (jobnameField == null) {
	// jobnameField = new JTextField();
	// jobnameField.setColumns(10);
	// }
	// return jobnameField;
	// }
	//
	// private JLabel getJobnameLabel() {
	// if (jobnameLabel == null) {
	// jobnameLabel = new JLabel("Jobname:");
	// }
	// return jobnameLabel;
	// }
	//
	// private JobSubmissionLogPanel getJobSubmissionLogPanel() {
	// if (jobSubmissionLogPanel == null) {
	// jobSubmissionLogPanel = new JobSubmissionLogPanel();
	// }
	// return jobSubmissionLogPanel;
	// }
	//
	// private JLabel getLblDatabase() {
	// if (lblDatabase == null) {
	// lblDatabase = new JLabel("Database:");
	// }
	// return lblDatabase;
	// }
	//
	// private JLabel getLblFastaFile() {
	// if (lblFastaFile == null) {
	// lblFastaFile = new JLabel("Fasta file:");
	// }
	// return lblFastaFile;
	// }
	//
	// private JLabel getLblOfJobs() {
	// if (lblOfJobs == null) {
	// lblOfJobs = new JLabel("# of jobs");
	// }
	// return lblOfJobs;
	// }
	//
	// private JLabel getLblProgram() {
	// if (lblProgram == null) {
	// lblProgram = new JLabel("Program:");
	// }
	// return lblProgram;
	// }
	//
	// private JLabel getNoJobsLabel() {
	// if (noJobsLabel == null) {
	// noJobsLabel = new JLabel("n/a");
	// }
	// return noJobsLabel;
	// }
	//
	// public JPanel getPanel() {
	// return this;
	// }
	//
	// public String getPanelName() {
	// return "mpiBLAST- multiFASTA";
	// }
	//
	// private JComboBox getProgramCombobox() {
	// if (programCombobox == null) {
	// programCombobox = new JComboBox();
	// programCombobox.setModel(new DefaultComboBoxModel(new String[] {
	// "blastn", "blastp", "blastx", "tblastn", "tblastx" }));
	// }
	// return programCombobox;
	// }
	//
	// private JSlider getSlider() {
	// if (slider == null) {
	// slider = new JSlider();
	// slider.setPaintTicks(true);
	// slider.setPaintLabels(true);
	// slider.setEnabled(false);
	// slider.setMinimum(1);
	// slider.setMaximum(1);
	// slider.addChangeListener(new ChangeListener() {
	//
	// public void stateChanged(ChangeEvent e) {
	//
	// getNoJobsLabel().setText(
	// new Integer(slider.getValue()).toString());
	//
	// }
	// });
	// }
	// return slider;
	// }
	//
	// private JButton getSubmitButton() {
	// if (submitButton == null) {
	// submitButton = new JButton("Submit");
	// submitButton.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent arg0) {
	//
	// if ("Submit".equals(submitButton.getText())) {
	//
	// if (subThread != null) {
	// subThread.interrupt();
	// }
	//
	// subThread = new Thread() {
	// @Override
	// public void run() {
	//
	// try {
	// submitJob();
	// } catch (final BatchJobException e) {
	//
	// myLogger.error(e.getLocalizedMessage(), e);
	// cleanUpUI();
	// }
	// }
	// };
	// subThread.start();
	// submitButton.setText("Cancel");
	// hm.addHistoryEntry(MPIBLAST_BATCH_INPUT_FILES,
	// (String) getInputFileComboBox()
	// .getSelectedItem());
	// } else if ("Cancel".equals(submitButton.getText())) {
	// subThread.interrupt();
	// submitButton.setText("Ok");
	// } else if ("Ok".equals(submitButton.getText())) {
	//
	// cleanUpUI();
	//
	// }
	//
	// }
	//
	// });
	// }
	// return submitButton;
	// }
	//
	// public String getSupportedApplication() {
	// return "mpiBlast";
	// }
	//
	// private void parseFastaFile() throws FileTransactionException {
	//
	// List<String> currentFastaInput = null;
	//
	// try {
	// try {
	// currentFastaInput = FileUtils.readLines(fm
	// .downloadFile(currentFile.getUrl()));
	// } catch (final FileTransactionException e) {
	// throw e;
	// }
	//
	// final Iterator<String> it = currentFastaInput.iterator();
	// while (it.hasNext()) {
	// final String line = it.next();
	// if (StringUtils.isBlank(line.trim())) {
	// it.remove();
	// }
	// }
	//
	// currentParsedFastaInput = new LinkedList<List<String>>();
	// List<String> currentPart = null;
	// for (final String line : currentFastaInput) {
	// if (line.startsWith(">")) {
	// if ((currentPart != null) && (currentPart.size() > 0)) {
	// currentParsedFastaInput.add(currentPart);
	// }
	// currentPart = new LinkedList<String>();
	// }
	// if (currentPart == null) {
	// throw new IllegalArgumentException(
	// "Can't parse fasta file: "
	// + line
	// + " doesn't start or doesn't belong to another line that starts with >");
	// }
	// currentPart.add(line);
	// }
	// currentParsedFastaInput.add(currentPart);
	//
	// } catch (final IOException e) {
	// myLogger.error(e.getLocalizedMessage(), e);
	// }
	//
	// }
	//
	// protected GlazedFile popupFileDialogAndAskForFile() {
	//
	// getFileDialog().setVisible(true);
	//
	// final GlazedFile file = getFileDialog().getSelectedFile();
	// getFileDialog().clearSelection();
	//
	// final GlazedFile currentDir = getFileDialog().getCurrentDirectory();
	//
	// hm.addHistoryEntry(MPIBLAST_EXAMPLE_LAST_INPUT_FILE_DIR,
	// currentDir.getUrl());
	//
	// return file;
	// }
	//
	// public void propertyChange(PropertyChangeEvent evt) {
	//
	// if (evt.getPropertyName().equals(BatchJobObject.SUBMITTING)) {
	// if ((Boolean) (evt.getOldValue()) && !(Boolean) (evt.getNewValue())) {
	// getSubmitButton().setText("Ok");
	// }
	// }
	// }
	//
	// private void setInputFile(GlazedFile file) {
	//
	// currentFile = file;
	// getInputFileComboBox().setSelectedItem(file.getUrl());
	//
	// if (currentFile == null) {
	// return;
	// }
	//
	// getSlider().setEnabled(true);
	//
	// try {
	// parseFastaFile();
	//
	// getSlider().setMaximum(currentParsedFastaInput.size());
	//
	// getJobnameField().setText(
	// uem.calculateUniqueJobname(currentFile
	// .getNameWithoutExtension() + "_job"));
	//
	// } catch (final Exception e) {
	// JXErrorPane.showDialog(e);
	// return;
	// }
	// }
	//
	// public void setServiceInterface(ServiceInterface si) {
	//
	// this.si = si;
	// this.fm = GrisuRegistryManager.getDefault(si).getFileManager();
	// this.uem = GrisuRegistryManager.getDefault(si)
	// .getUserEnvironmentManager();
	// this.hm = GrisuRegistryManager.getDefault(si).getHistoryManager();
	// this.hm.setMaxNumberOfEntries(MPIBLAST_BATCH_INPUT_FILES, 8);
	//
	// cleanUpUI();
	// }
	//
	// public void submitJob() throws BatchJobException {
	//
	// if (currentBatchJob != null) {
	// currentBatchJob.removePropertyChangeListener(this);
	// }
	//
	// currentBatchJob = new BatchJobObject(si, getJobnameField().getText(),
	// "/ACC", "mpiblast", "1.5.0");
	//
	// currentBatchJob.addPropertyChangeListener(this);
	//
	// getJobSubmissionLogPanel().setBatchJob(currentBatchJob);
	//
	// final Map<String, List<List<String>>> inputFiles = new
	// LinkedHashMap<String, List<List<String>>>();
	//
	// final int noJobs = getSlider().getValue();
	//
	// final Double linesPerJobD = new Double(currentParsedFastaInput.size())
	// / new Double(noJobs);
	//
	// final int linesPerJob = new Long(Math.round(linesPerJobD + 0.499999))
	// .intValue();
	//
	// for (int i = 0; i < currentParsedFastaInput.size(); i = i + linesPerJob)
	// {
	// int end = i + linesPerJob;
	// if (end > currentParsedFastaInput.size()) {
	// end = currentParsedFastaInput.size();
	// }
	// final List<List<String>> tempList = currentParsedFastaInput
	// .subList(i, end);
	// inputFiles.put(
	// "part" + formatter.format(i) + "-part"
	// + formatter.format(end - 1), tempList);
	// }
	//
	// for (final String jobname : inputFiles.keySet()) {
	//
	// // create temp file
	// final String inputFIlename = jobname + "_"
	// + currentBatchJob.getJobname();
	// final File tempFile = new File(
	// System.getProperty("java.io.tmpdir"), inputFIlename);
	// tempFile.delete();
	// try {
	// final List<List<String>> all = inputFiles.get(jobname);
	// final List<String> consolidated = new LinkedList<String>();
	// for (final List<String> temp : all) {
	// consolidated.addAll(temp);
	// }
	// FileUtils.writeLines(tempFile, consolidated);
	// } catch (final IOException e) {
	// throw new BatchJobException(e);
	// }
	//
	// final JobObject tempJob = new JobObject(si);
	// tempJob.setJobname(uem.calculateUniqueJobname(jobname + "_"
	// + currentBatchJob.getJobname()));
	// tempJob.addInputFileUrl(tempFile.toURI().toString());
	// tempJob.setApplication("mpiblast");
	// tempJob.setApplicationVersion("1.5.0");
	// // tempJob.setWalltimeInSeconds(604800);
	// tempJob.setWalltimeInSeconds(DEFAULT_WALLTIME);
	// final String commandline = "mpiblast -p blastp -d nr -i "
	// + inputFIlename + " -o " + jobname + ".out.txt";
	// tempJob.setCommandline(commandline);
	// tempJob.setForce_mpi(true);
	// tempJob.setCpus(8);
	//
	// currentBatchJob.addJob(tempJob);
	// }
	//
	// currentBatchJob.setDefaultNoCpus(8);
	// // currentBatchJob.setDefaultWalltimeInSeconds(604800);
	// currentBatchJob.setDefaultWalltimeInSeconds(DEFAULT_WALLTIME);
	//
	// try {
	// currentBatchJob.prepareAndCreateJobs(true);
	// } catch (final Exception e) {
	// throw new BatchJobException(e);
	// }
	//
	// try {
	// currentBatchJob.submit();
	// } catch (final Exception e) {
	// throw new BatchJobException(e);
	// }
	//
	// currentBatchJob.addJobProperty(Constants.JOB_RESULT_FILENAME_PATTERNS,
	// ".out.txt");
	//
	// }
}
