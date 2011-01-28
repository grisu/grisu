package grisu.model.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains a list of {@link DtoApplicationDetail} objects.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "applicationdetails")
public class DtoApplicationDetails {

	public static DtoApplicationDetails createDetails(String appName,
			Map<String, String> details) {

		final DtoApplicationDetails result = new DtoApplicationDetails();
		result.setApplicationName(appName);
		final List<DtoApplicationDetail> list = new LinkedList<DtoApplicationDetail>();
		for (final String key : details.keySet()) {
			final DtoApplicationDetail temp = new DtoApplicationDetail();
			temp.setKey(key);
			temp.setValue(details.get(key));
			list.add(temp);
		}
		result.setDetails(list);

		return result;
	}

	/**
	 * A list of all the application details.
	 */
	private List<DtoApplicationDetail> details = new LinkedList<DtoApplicationDetail>();

	/**
	 * The name of the application.
	 */
	private String applicationName;

	@XmlAttribute
	public String getApplicationName() {
		return applicationName;
	}

	@XmlElement(name = "detail")
	public List<DtoApplicationDetail> getDetails() {
		return details;
	}

	public Map<String, String> getDetailsAsMap() {

		final Map<String, String> map = new HashMap<String, String>();
		for (final DtoApplicationDetail detail : getDetails()) {
			map.put(detail.getKey(), detail.getValue());
		}
		return map;

	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setDetails(List<DtoApplicationDetail> details) {
		this.details = details;
	}

}
