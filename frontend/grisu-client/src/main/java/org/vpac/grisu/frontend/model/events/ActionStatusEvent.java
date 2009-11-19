package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.model.dto.DtoActionStatus;

public class ActionStatusEvent {
	
	private DtoActionStatus status;

	
	public ActionStatusEvent(DtoActionStatus status) {
		this.status = status;
	}
	
	public double getPercentFinished() {
		return this.status.percentFinished();
	}

}
