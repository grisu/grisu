package grisu.model.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that contains a list of {@link DtoDataLocation} objects.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "datalocations")
public class DtoDataLocations {

	public static DtoDataLocations createDataLocations(String fqan,
			Map<String, String[]> dataLocationsMap) {

		final DtoDataLocations result = new DtoDataLocations();

		final List<DtoDataLocation> list = new LinkedList<DtoDataLocation>();

		for (final String key : dataLocationsMap.keySet()) {
			final DtoDataLocation temp = new DtoDataLocation();
			temp.setRooturl(key);
			temp.setFqan(fqan);
			temp.setPaths(Arrays.asList(dataLocationsMap.get(key)));
			list.add(temp);
		}

		result.setDataLocations(list);

		return result;
	}

	/**
	 * The list of datalocations.
	 */
	private List<DtoDataLocation> dataLocations = new LinkedList<DtoDataLocation>();

	@XmlElement(name = "datalocation")
	public List<DtoDataLocation> getDataLocations() {
		return dataLocations;
	}

	public void setDataLocations(List<DtoDataLocation> dataLocations) {
		this.dataLocations = dataLocations;
	}

}
