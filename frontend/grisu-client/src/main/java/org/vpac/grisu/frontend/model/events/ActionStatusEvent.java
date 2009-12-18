package org.vpac.grisu.frontend.model.events;

import org.vpac.grisu.model.dto.DtoActionStatus;

public class ActionStatusEvent {

	private DtoActionStatus status;
	private String prefix;

	public ActionStatusEvent(DtoActionStatus status) {
		this.status = status;
	}

	public ActionStatusEvent(DtoActionStatus status, String prefix) {
		this.status = status;
		this.prefix = prefix;
	}

	public double getPercentFinished() {
		return this.status.percentFinished();
	}

	public String getPrefix() {
		if (prefix == null) {
			return "";
		} else {
			return prefix;
		}
	}

}