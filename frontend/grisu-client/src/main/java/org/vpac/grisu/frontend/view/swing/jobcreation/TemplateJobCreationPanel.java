package org.vpac.grisu.frontend.view.swing.jobcreation;

import java.awt.CardLayout;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.PanelConfig;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateException;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateHelpers;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.TemplateObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.templates.inputPanels.AbstractInputPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TemplateJobCreationPanel extends JPanel implements JobCreationPanel {

	public static final String LOADING_PANEL = "loading";
	public static final String TEMPLATE_PANEL = "template";

	private TemplateObject template;
	private final List<String> lines;
	private LinkedHashMap<String, PanelConfig> panelConfigs;
	private final CardLayout cardLayout = new CardLayout();
	private JPanel loadingPanel;
	private JProgressBar progressBar;
	private JLabel label;
	private JPanel currentTemplatePanel;

	public TemplateJobCreationPanel(List<String> lines)  {
		this.lines = lines;
		try {
			this.panelConfigs = TemplateHelpers.parseConfig(lines);
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		setLayout(cardLayout);
		add(getLoadingPanel(), LOADING_PANEL);
	}


	public boolean createsBatchJob() {
		return false;
	}

	public boolean createsSingleJob() {
		return true;
	}

	private JLabel getLabel() {
		if (label == null) {
			label = new JLabel("Loading template...");
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return label;
	}

	private JPanel getLoadingPanel() {
		if (loadingPanel == null) {
			loadingPanel = new JPanel();
			loadingPanel.setLayout(new FormLayout(new ColumnSpec[] {
					ColumnSpec.decode("24dlu"),
					ColumnSpec.decode("default:grow"),
					ColumnSpec.decode("24dlu"),},
					new RowSpec[] {
					RowSpec.decode("4dlu:grow"),
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					RowSpec.decode("4dlu:grow"),}));
			loadingPanel.add(getProgressBar(), "2, 2");
			loadingPanel.add(getLabel(), "2, 4, fill, default");
		}
		return loadingPanel;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getPanelName() {

		for ( PanelConfig config : panelConfigs.values() ) {
			String bean = config.getConfig().get(AbstractInputPanel.NAME);
			if ( AbstractInputPanel.TEMPLATENAME.equals(bean)) {
			}
		}
		return getSupportedApplication();
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
		}
		return progressBar;
	}
	public String getSupportedApplication() {

		for ( PanelConfig config : panelConfigs.values() ) {
			String bean = config.getConfig().get(AbstractInputPanel.BEAN);
			if ( AbstractInputPanel.APPLICATION.equals(bean) ) {
				String app = config.getConfig().get(AbstractInputPanel.DEFAULT_VALUE);
				if ( StringUtils.isNotBlank(app) ) {
					return app;
				} else {
					break;
				}
			}
		}
		return "generic";
	}
	public void setServiceInterface(ServiceInterface si) {

		try {
			if ( currentTemplatePanel != null ) {
				remove(currentTemplatePanel);
			}
			template = TemplateHelpers.parseAndCreateTemplatePanel(si, lines);
			currentTemplatePanel = template.getTemplatePanel();
			add(currentTemplatePanel, TEMPLATE_PANEL);
			cardLayout.show(this, TEMPLATE_PANEL);
		} catch (TemplateException e) {
			e.printStackTrace();
			cardLayout.show(this, LOADING_PANEL);
		}

	}
}
