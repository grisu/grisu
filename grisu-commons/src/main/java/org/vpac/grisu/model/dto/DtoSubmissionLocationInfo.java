package org.vpac.grisu.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper object for the submission location.
 *
 * At the moment this only holds the submissionlocation itself, but I may add things like
 * sitename and such later on.
 * 
 * @author Markus Binsteiner
 *
 */
@XmlRootElement(name="submissionlocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoSubmissionLocationInfo {
	
	/**
	 * The submission location string. The format for this String is:
	 *            queue:host[#porttype]
	 */
	@XmlAttribute(name="name")
	private String submissionLocation;

	public String getSubmissionLocation() {
		return submissionLocation;
	}

	public void setSubmissionLocation(String submissionLocation) {
		this.submissionLocation = submissionLocation;
	}

}
