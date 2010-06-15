package org.vpac.grisu.control.events;

public class DefaultFqanChangedEvent {

	private String newFqan = null;

	public DefaultFqanChangedEvent(String newFqan) {
		this.newFqan = newFqan;
	}

	public String getNewFqan() {
		return this.newFqan;
	}

}
