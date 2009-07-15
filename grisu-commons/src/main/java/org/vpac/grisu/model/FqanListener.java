package org.vpac.grisu.model;

/**
 * An interface for classes that want to monitor fqan events.
 * 
 * @author markus
 */
public interface FqanListener {

	public void fqansChanged(FqanEvent event);

}
