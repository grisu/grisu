package org.vpac.grisu.model.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.vpac.grisu.control.JobConstants;

/**
 * This one holds information about a job that was created (and maybe already submitted to the endpoint resource.
 * 
 * You can use this to query information like job-directory and status of the job. Have a look in the 
 * Constants class in the GlueInterface module of the Infosystems project
 * for values of keys of possible job properties.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="job")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoJob {
	
	public static DtoJob createJob(int status, Map<String, String> jobProperties) {
		
		DtoJob result = new DtoJob();
		
		result.setStatus(status);
		result.setStatusString(JobConstants.translateStatus(status));
		List<DtoJobProperty> list = new LinkedList<DtoJobProperty>();
		for ( String key : jobProperties.keySet() ) {
			DtoJobProperty temp = new DtoJobProperty();
			temp.key = key;
			temp.value = jobProperties.get(key);
			list.add(temp);
		}
		
		result.setProperties(list);
		return result;
	}
	

	/**
	 * The list of job properties.
	 */
	@XmlElement(name="jobproperty")
	private List<DtoJobProperty> properties = new LinkedList<DtoJobProperty>();
	/**
	 * The status of the job. Be aware that, depending on how you queried for this job, this can be stale information.
	 */
	@XmlElement(name="status")
	private int status;
	/**
	 * The status of the job as a string.
	 */
	@XmlElement(name="statusstring")
	private String statusString;

	public List<DtoJobProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DtoJobProperty> properties) {
		this.properties = properties;
	}
	
	public Map<String, String> getPropertiesAsMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		for ( DtoJobProperty prop : getProperties() ) {
			map.put(prop.key, prop.value);
		}
		
		return map;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

}
