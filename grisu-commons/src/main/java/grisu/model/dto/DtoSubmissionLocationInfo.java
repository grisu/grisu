package grisu.model.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper object for the submission location.
 * 
 * At the moment this only holds the submissionlocation itself, but I may add
 * things like sitename and such later on.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "submissionlocation")
public class DtoSubmissionLocationInfo {

	/**
	 * The submission location string. The format for this String is:
	 * queue:host[#porttype]
	 */
	private String submissionLocation;

	@XmlAttribute(name = "name")
	public String getSubmissionLocation() {
		return submissionLocation;
	}

	public void setSubmissionLocation(String submissionLocation) {
		this.submissionLocation = submissionLocation;
	}

}
