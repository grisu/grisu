package org.vpac.grisu.client.view.swing.template.modules.genericAuto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.ValueListener;
import org.vpac.grisu.client.view.swing.template.panels.Version;
import org.vpac.grisu.control.GrisuRegistry;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.js.model.utils.SubmissionLocationHelpers;
import org.vpac.grisu.model.EnvironmentSnapshotValues;
import org.vpac.grisu.model.GridResource;
import org.vpac.grisu.model.UserApplicationInformation;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GridResourceSuggestionPanel extends JPanel implements TemplateNodePanel, ValueListener {
	
	private JLabel label3;
	private JLabel label2;
	static final Logger myLogger = Logger.getLogger(GridResourceSuggestionPanel.class.getName());
	
	private UserApplicationInformation infoObject = null;
	private EnvironmentSnapshotValues esv = GrisuRegistry.getDefault()
	.getEnvironmentSnapshotValues();

	private JLabel label;
	
	private Version versionPanel = null;
	private TemplateNode templateNode = null;
	
	/**
	 * Create the panel
	 */
	public GridResourceSuggestionPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("69px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("15px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default")}));
		add(getLabel(), new CellConstraints("2, 2, 1, 1, fill, fill"));
		add(getLabel2(), new CellConstraints(2, 4));
		add(getLabel3(), new CellConstraints(2, 6));
		//
	}
	
	
	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("label1");
		}
		return label;
	}
	
	private Version getVersionPanel() {

		if (versionPanel == null) {
			try {
				// try to find a templateNodevalueSetter that is a Version panel
				for (TemplateNode node : this.templateNode.getTemplate()
						.getTemplateNodes().values()) {
					if (node.getTemplateNodeValueSetter() instanceof Version) {
						versionPanel = (Version) node
								.getTemplateNodeValueSetter();
						break;
					}

				}
			} catch (Exception e) {
				myLogger.warn("Couldn't initialize version panel yet...");
				versionPanel = null;
				return null;
			}
			if (versionPanel != null) {
				versionPanel.addValueListener(this);
			}
		}
		return versionPanel;
	}
	public void valueChanged(TemplateNodePanel panel, String newValue) {

		System.out.println("Value changed: "+newValue);
		Map<String, String> tempJobProperties = new HashMap<String, String>();
		tempJobProperties.put(JobConstants.APPLICATIONVERSION_KEY, newValue);
		List<GridResource> resources = infoObject.getBestSubmissionLocations(tempJobProperties, esv.getCurrentFqan());
		
		try {
			getLabel().setText(SubmissionLocationHelpers.createSubmissionLocationString(resources.get(0))+" "+resources.get(0).getRank());
			getLabel2().setText(SubmissionLocationHelpers.createSubmissionLocationString(resources.get(1))+" "+resources.get(1).getRank());
			getLabel3().setText(SubmissionLocationHelpers.createSubmissionLocationString(resources.get(2))+" "+resources.get(2).getRank());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @return
	 */
	protected JLabel getLabel2() {
		if (label2 == null) {
			label2 = new JLabel();
			label2.setText("label2");
		}
		return label2;
	}
	/**
	 * @return
	 */
	protected JLabel getLabel3() {
		if (label3 == null) {
			label3 = new JLabel();
			label3.setText("New JLabel");
		}
		return label3;
	}

	public void addValueListener(ValueListener v) {
		// TODO Auto-generated method stub
		
	}

	public JPanel getTemplateNodePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeValueListener(ValueListener v) {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		this.templateNode = node;
		this.infoObject = GrisuRegistry.getDefault()
		.getUserApplicationInformation(templateNode.getTemplate().getApplicationName());
		getVersionPanel();
		
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public String getExternalSetValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExternalSetValue(String value) {
		// TODO Auto-generated method stub
		
	}
	

}
