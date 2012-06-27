package grisu.frontend.view.swing.jobmonitoring.single;

import javax.swing.JPanel;

public class JobDetailPanelSmall extends JPanel {
	// private JLabel lblStatus;
	// private JTextField textField_1;
	//
	// private JobObject job;
	// private JideTabbedPane jideTabbedPane;
	// private JScrollPane scrollPane;
	// private JTextArea propertiesTextArea;
	// private JScrollPane scrollPane_1;
	// private JTextArea logTextArea;
	// private FileListWithPreviewPanel fileListWithPreviewPanel;
	//
	// private final ServiceInterface si;
	//
	// /**
	// * Create the panel.
	// *
	// * @wbp.parser.constructor
	// */
	// public JobDetailPanelSmall(ServiceInterface si) {
	// setBorder(new TitledBorder(null, "Job: n/a", TitledBorder.LEADING,
	// TitledBorder.TOP, null, null));
	//
	// this.si = si;
	//
	// setLayout(new FormLayout(new ColumnSpec[] {
	// FormFactory.RELATED_GAP_COLSPEC,
	// ColumnSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_COLSPEC,
	// ColumnSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
	// FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
	// FormFactory.RELATED_GAP_ROWSPEC,
	// RowSpec.decode("default:grow"),
	// FormFactory.RELATED_GAP_ROWSPEC, }));
	// add(getLblStatus(), "2, 2, right, default");
	// add(getTextField_1(), "4, 2, fill, default");
	// add(getJideTabbedPane(), "2, 4, 3, 1, fill, fill");
	//
	// }
	//
	// public JobDetailPanelSmall(ServiceInterface si, JobObject job) {
	// this(si);
	// setJob(job);
	// }
	//
	// private FileListWithPreviewPanel getFileListWithPreviewPanel() {
	// if (fileListWithPreviewPanel == null) {
	// fileListWithPreviewPanel = new FileListWithPreviewPanel(si, null,
	// null, false, true, false, false, false);
	// }
	// return fileListWithPreviewPanel;
	// }
	//
	// private JideTabbedPane getJideTabbedPane() {
	// if (jideTabbedPane == null) {
	// jideTabbedPane = new JideTabbedPane();
	// jideTabbedPane.setTabPlacement(SwingConstants.TOP);
	// jideTabbedPane.addTab("Job directory", null,
	// getFileListWithPreviewPanel(), null);
	// jideTabbedPane.addTab("Properties", getScrollPane_1());
	// jideTabbedPane.addTab("Log", getScrollPane_1_1());
	// }
	// return jideTabbedPane;
	// }
	//
	// private JLabel getLblStatus() {
	// if (lblStatus == null) {
	// lblStatus = new JLabel("Status");
	// }
	// return lblStatus;
	// }
	//
	// private JTextArea getLogTextArea() {
	// if (logTextArea == null) {
	// logTextArea = new JTextArea();
	// }
	// return logTextArea;
	// }
	//
	// public JPanel getPanel() {
	// return this;
	// }
	//
	// private JTextArea getPropertiesTextArea() {
	// if (propertiesTextArea == null) {
	// propertiesTextArea = new JTextArea();
	// }
	// return propertiesTextArea;
	// }
	//
	// private JScrollPane getScrollPane_1() {
	// if (scrollPane == null) {
	// scrollPane = new JScrollPane();
	// scrollPane.setViewportView(getPropertiesTextArea());
	// }
	// return scrollPane;
	// }
	//
	// private JScrollPane getScrollPane_1_1() {
	// if (scrollPane_1 == null) {
	// scrollPane_1 = new JScrollPane();
	// scrollPane_1.setViewportView(getLogTextArea());
	// }
	// return scrollPane_1;
	// }
	//
	// private JTextField getTextField_1() {
	// if (textField_1 == null) {
	// textField_1 = new JTextField();
	// textField_1.setEditable(false);
	// textField_1.setColumns(10);
	// }
	// return textField_1;
	// }
	//
	// public void propertyChange(PropertyChangeEvent evt) {
	//
	// if (evt.getPropertyName().equals("status")) {
	// getTextField_1()
	// .setText(
	// JobConstants.translateStatus((Integer) (evt
	// .getNewValue())));
	// }
	// }
	//
	// public void setJob(JobObject job) {
	//
	// this.job = job;
	//
	// getFileListWithPreviewPanel().setRootUrl(job.getJobDirectoryUrl());
	// getFileListWithPreviewPanel().setCurrentUrl(job.getJobDirectoryUrl());
	// setBorder(new TitledBorder(null, job.getJobname(),
	// TitledBorder.LEADING, TitledBorder.TOP, null, null));
	// getTextField_1().setText(
	// JobConstants.translateStatus(job.getStatus(false)));
	//
	// setProperties();
	// setLog();
	//
	// }
	//
	// private void setLog() {
	// final StringBuffer temp = new StringBuffer();
	//
	// final Map<Date, String> log = job.getLogMessages(true);
	// for (final Date date : log.keySet()) {
	// temp.append(date.toString() + ":\t" + log.get(date) + "\n");
	// }
	//
	// getLogTextArea().setText(temp.toString());
	// }
	//
	// private void setProperties() {
	//
	// final StringBuffer temp = new StringBuffer();
	//
	// final Map<String, String> prop = job.getAllJobProperties(true);
	// for (final String key : prop.keySet()) {
	// temp.append(key + "\t\t" + prop.get(key) + "\n");
	// }
	// getPropertiesTextArea().setText(temp.toString());
	// }
}
