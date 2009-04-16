

package org.vpac.grisu.control;

import java.util.EventObject;

public class FqanEvent extends EventObject {
	
	public static final int FQAN_ADDED = 0;
	public static final int FQAN_REMOVED = 1;
	public static final int FQANS_REFRESHED = 2;
	public static final int DEFAULT_FQAN_CHANGED = 3;
	
	private int event_type = -1;
	private String fqan = null;
	private String[] fqans = null;
	
	public FqanEvent(Object source, int event_type, String fqan) {
		super(source);
		this.event_type = event_type;
		this.fqan = fqan;
	}
	
	public FqanEvent(Object source, String[] fqans) {
		super(source);
		this.event_type = FQANS_REFRESHED;
		this.fqans = fqans;
	}

	public int getEvent_type() {
		return event_type;
	}

	public String getFqan() {
		return fqan;
	}

	public String[] getFqans() {
		return fqans;
	}

}
