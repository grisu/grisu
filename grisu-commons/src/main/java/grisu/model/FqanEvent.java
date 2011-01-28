package grisu.model;

import java.util.EventObject;

/**
 * Event that is sent when something VO related changes in the users'
 * environment.
 * 
 * @author markus
 * 
 */
public class FqanEvent extends EventObject {

	public static final int FQAN_ADDED = 0;
	public static final int FQAN_REMOVED = 1;
	public static final int FQANS_REFRESHED = 2;
	public static final int DEFAULT_FQAN_CHANGED = 3;

	private int event_type = -1;
	private String fqan = null;
	private String[] fqans = null;

	public FqanEvent(final Object source, final int event_type,
			final String fqan) {
		super(source);
		this.event_type = event_type;
		this.fqan = fqan;
	}

	public FqanEvent(final Object source, final String[] fqans) {
		super(source);
		this.event_type = FQANS_REFRESHED;
		this.fqans = fqans;
	}

	public final int getEvent_type() {
		return event_type;
	}

	public final String getFqan() {
		return fqan;
	}

	public final String[] getFqans() {
		return fqans;
	}

}
