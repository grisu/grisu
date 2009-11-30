package org.vpac.grisu.model.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public interface DtoRemoteObject {
	
	
	public String getRootUrl();
	public String getName();

	public boolean isFolder();
	
//	public List<DtoRemoteObject> listAllChildren();
	
}
