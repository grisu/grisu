package grisu.frontend.view.swing.files.preview.fileViewers;

import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.view.swing.files.preview.GridFileViewer;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class JobStatusGridFileViewer extends JPanel implements GridFileViewer {

	static final Logger myLogger = LoggerFactory
			.getLogger(JobStatusGridFileViewer.class.getName());

	private final static ImageIcon REFRESH_ICON = createImageIcon(
			"refresh.png", "Refresh");

	private static ChartPanel createChart(String title, String y_axis,
			XYDataset dataset, boolean createLegend) {

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // title
				"Date", // x-axis label
				y_axis, // y-axis label
				dataset, // data
				createLegend, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		final XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
		}

		final DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("dd.MM. HH:mm"));

		return new ChartPanel(chart);

	}

	protected static ImageIcon createImageIcon(String path, String description) {

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(JobStatusGridFileViewer.class.getClassLoader()
					.getResource(path));
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}

		return icon;

	}

	private File csvFile = null;

	private final TimeSeriesCollection cpusDataset = new TimeSeriesCollection();
	private final TimeSeriesCollection licensesDataset = new TimeSeriesCollection();
	private final TimeSeriesCollection ligandsDataset = new TimeSeriesCollection();
	private final TimeSeries cpusSeries = new TimeSeries("Cpus used");
	private final TimeSeries licensesUserSeries = new TimeSeries(
			"Licenses used (for job)");
	private final TimeSeries licensesAllSeries = new TimeSeries(
			"Licenses used (overall)");
	private final TimeSeries ligandsSeries = new TimeSeries(
			"Lingands processed");

	private ChartPanel cpusChart;
	private ChartPanel licensesChart;
	private ChartPanel ligandsChart;
	private JLabel label;
	private JButton btnRefresh;

	private FileManager fm;
	private String url;

	private boolean showMinutes = false;
	private JCheckBox chckbxShowMinutes;
	private JLabel label_1;

	/**
	 * Create the panel.
	 */
	public JobStatusGridFileViewer() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC, }));
		cpusDataset.addSeries(cpusSeries);
		licensesDataset.addSeries(licensesUserSeries);
		licensesDataset.addSeries(licensesAllSeries);
		ligandsDataset.addSeries(ligandsSeries);
		add(getCpusChart(), "2, 4, 3, 1");
		add(getLicensesChart(), "2, 6, 3, 1");
		add(getLigandsChart(), "2, 2, 3, 1");
		add(getChckbxShowMinutes(), "2, 8, left, default");
		add(getBtnRefresh(), "4, 8, right, default");
	}

	private synchronized void generateGraph() {

		cpusSeries.clear();
		licensesUserSeries.clear();
		licensesAllSeries.clear();
		ligandsSeries.clear();

		List<String> lines;
		try {
			lines = FileUtils.readLines(this.csvFile);
		} catch (final IOException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return;
		}

		for (int i = 0; i < lines.size(); i++) {

			final String[] tokens = lines.get(i).split(",");
			final Date date = new Date(Long.parseLong(tokens[0]) * 1000);
			int cpus = Integer.parseInt(tokens[1]);
			if (cpus < 0) {
				cpus = 0;
			}
			int licensesUser = Integer.parseInt(tokens[2]);
			if (licensesUser < 0) {
				licensesUser = 0;
			}
			int licensesAll = Integer.parseInt(tokens[3]);
			if (licensesAll < 0) {
				licensesAll = 0;
			}
			final int ligands = Integer.parseInt(tokens[4]);

			RegularTimePeriod p = null;
			if (showMinutes) {
				p = new Minute(date);
			} else {
				p = new Hour(date);
			}

			cpusSeries.addOrUpdate(p, cpus);
			licensesUserSeries.addOrUpdate(p, licensesUser);
			licensesAllSeries.addOrUpdate(p, licensesAll);
			ligandsSeries.addOrUpdate(p, ligands);

		}

	}

	private JButton getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new JButton("Refresh");
			btnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if ((fm == null) || (url == null)) {
						return;
					}

					new Thread() {
						@Override
						public void run() {
							getBtnRefresh().setEnabled(false);
							File temp;
							try {
								temp = fm.downloadFile(url);
							} catch (final FileTransactionException e1) {
								final ErrorInfo ei = new ErrorInfo(
										"Download error",
										"Error while trying to download job status file.",
										e1.getLocalizedMessage(),
										(String) null, e1, Level.SEVERE,
										(Map) null);
								JXErrorPane.showDialog(
										JobStatusGridFileViewer.this, ei);
								return;
							}

							setFile(null, temp);
							getBtnRefresh().setEnabled(true);
						}
					}.start();
				}
			});
			btnRefresh.setIcon(REFRESH_ICON);
		}
		return btnRefresh;
	}

	private JCheckBox getChckbxShowMinutes() {
		if (chckbxShowMinutes == null) {
			chckbxShowMinutes = new JCheckBox("Display minutes");
			chckbxShowMinutes.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (chckbxShowMinutes.isSelected()) {
						showMinutes = true;
					} else {
						showMinutes = false;
					}

					new Thread() {
						@Override
						public void run() {
							chckbxShowMinutes.setEnabled(false);
							setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							generateGraph();
							setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							chckbxShowMinutes.setEnabled(true);
						}
					}.start();

				}
			});
		}
		return chckbxShowMinutes;
	}

	public ChartPanel getCpusChart() {
		if (cpusChart == null) {
			cpusChart = createChart("Cpus in use", "# cpus", cpusDataset, false);
		}
		return cpusChart;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("New label");
		}
		return label;
	}

	private JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel("New label");
		}
		return label_1;
	}

	public ChartPanel getLicensesChart() {
		if (licensesChart == null) {
			licensesChart = createChart("Licenses in use", "# licenses",
					licensesDataset, true);
		}
		return licensesChart;
	}

	public ChartPanel getLigandsChart() {
		if (ligandsChart == null) {
			ligandsChart = createChart("Ligands processed", "# lignads",
					ligandsDataset, false);
		}
		return ligandsChart;
	}

	public JPanel getPanel() {
		return this;
	}

	public String[] getSupportedMimeTypes() {
		return new String[] { "text/comma-separated-values", "text/csv" };
	}

	public void setFile(GridFile file, File localCacheFile) {

		this.csvFile = localCacheFile;
		if (csvFile == null) {
			return;
		}

		generateGraph();
	}

	public void setFileManagerAndUrl(FileManager fm, String url) {
		this.fm = fm;
		this.url = url;
	}

	public void setShowMinutes(boolean m) {
		this.showMinutes = m;
	}
}
