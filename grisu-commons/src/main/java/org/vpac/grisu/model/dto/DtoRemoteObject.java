package org.vpac.grisu.model.dto;

import java.util.List;

public interface DtoRemoteObject {
	
	public String getUrl();
	public String getName();

	public boolean isFolder();
	
	public List<DtoRemoteObject> getChildren();
	
}
