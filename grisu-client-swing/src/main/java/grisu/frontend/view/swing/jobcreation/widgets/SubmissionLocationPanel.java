package grisu.frontend.view.swing.jobcreation.widgets;

import grisu.control.ServiceInterface;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.jcommons.interfaces.GridResource;
import grisu.model.FqanEvent;
import grisu.model.GrisuRegistryManager;
import grisu.model.info.UserApplicationInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class SubmissionLocationPanel extends AbstractWidget implements
EventSubscriber<FqanEvent> {

	public static final String NOT_READY_STRING = "Loading...";

	private JComboBox comboBox;

	private String currentVO = null;

	private String application = Constants.GENERIC_APPLICATION_NAME;
	private String version = Constants.NO_VERSION_INDICATOR_STRING;

	private final DefaultComboBoxModel subLocModel = new DefaultComboBoxModel();

	private UserApplicationInformation appInfo = null;

	private boolean subLocsFilled = false;

	/**
	 * Create the panel.
	 */
	public SubmissionLocationPanel() {
		super();
		setBorder(new TitledBorder(null, "Submit to:", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, }));
		subLocModel.addElement(NOT_READY_STRING);
		add(getComboBox(), "2, 2, fill, default");
		EventBus.subscribe(FqanEvent.class, this);

	}

	private void fillSubmissionLocations() {

		final Object oldValue = subLocModel.getSelectedItem();

		subLocModel.removeAllElements();
		subLocModel.addElement(NOT_READY_STRING);

		if (getServiceInterface() == null) {
			return;
		}

		appInfo = GrisuRegistryManager.getDefault(getServiceInterface()).getUserApplicationInformation(this.application);

		new Thread() {
			@Override
			public void run() {

				Map<JobSubmissionProperty, String> additional = new HashMap<JobSubmissionProperty, String>();
				additional.put(JobSubmissionProperty.APPLICATIONVERSION,
						version);
				Set<GridResource> subLocs = appInfo
				.getAllSubmissionLocationsAsGridResources(
						additional,
						currentVO);

				subLocModel.removeAllElements();

				for (GridResource gr : subLocs ) {
					subLocModel.addElement(gr);
				}

				if ((oldValue instanceof GridResource)
						&& subLocs.contains(oldValue)) {
					subLocModel.setSelectedItem(oldValue);
				}

				subLocsFilled = true;
			}
		}.start();


	}


	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(subLocModel);
			comboBox.setEditable(false);
		}
		return comboBox;
	}

	@Override
	public String getValue() {

		// TODO
		return null;
	}

	public void onEvent(FqanEvent event) {

		String oldVo = currentVO;
		currentVO = event.getFqan();

		if (!StringUtils.equals(oldVo, currentVO)) {
			fillSubmissionLocations();
		}
	}

	public void setApplication(String application) {
		String old = this.application;
		this.application = application;

		if (!StringUtils.equals(old, this.application) || !subLocsFilled) {
			fillSubmissionLocations();
		}
	}

	@Override
	public void setServiceInterface(ServiceInterface si) {
		super.setServiceInterface(si);

		this.currentVO = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager().getCurrentFqan();

		fillSubmissionLocations();
	}

	@Override
	public void setValue(String value) {
		// TODO Auto-generated method stub

	}

	public void setVersion(String version) {
		String old = this.version;
		this.version = version;

		if (!StringUtils.equals(old, this.version) || !subLocsFilled) {
			fillSubmissionLocations();
		}

	}
}
