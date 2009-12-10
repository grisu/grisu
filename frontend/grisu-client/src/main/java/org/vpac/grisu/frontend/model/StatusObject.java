package org.vpac.grisu.frontend.model;

import java.util.HashSet;
import java.util.Set;

import org.bushe.swing.event.EventBus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.events.ActionStatusEvent;
import org.vpac.grisu.frontend.model.events.SystemOutStatusLogger;
import org.vpac.grisu.model.dto.DtoActionStatus;

public class StatusObject {

	public enum Listener {
		STDOUT
	}

	private final ServiceInterface si;
	private final String handle;
	private final Set<Listener> listeners;

	private DtoActionStatus lastStatus;

	public StatusObject(ServiceInterface si, String handle,
			Set<Listener> listeners) {
		this.si = si;
		this.handle = handle;
		this.listeners = listeners;

		if (listeners != null) {
			for (Listener l : listeners) {
				addListener(l);
			}
		}
	}

	public StatusObject(ServiceInterface si, String handle, Listener l) {
		this(si, handle, new HashSet<Listener>());
		addListener(l);
	}

	public void addListener(Listener l) {

		if (l != null) {
			switch (l) {
			case STDOUT:
				new SystemOutStatusLogger(handle);
				break;
			}
		}

	}

	public StatusObject(ServiceInterface si, String handle) {
		this(si, handle, new HashSet<Listener>());
	}

	public DtoActionStatus getStatus() {

		lastStatus = si.getActionStatus(handle);
		return lastStatus;
	}

	public void waitForActionToFinish(int recheckIntervalInSeconds,
			boolean exitIfFailed, boolean sendStatusEvent) {
		waitForActionToFinish(recheckIntervalInSeconds, exitIfFailed, sendStatusEvent, null);
	}
	
	public void waitForActionToFinish(int recheckIntervalInSeconds,
				boolean exitIfFailed, boolean sendStatusEvent, String statusMessagePrefix) {

		while (!(lastStatus = si.getActionStatus(handle)).isFinished()) {
			if (sendStatusEvent) {
				EventBus.publish(handle, new ActionStatusEvent(lastStatus, "Submissionstatus: "));
			}

			if (exitIfFailed) {
				if (lastStatus.isFailed()) {
					return;
				}
			}

			try {
				Thread.sleep(recheckIntervalInSeconds * 1000);
			} catch (InterruptedException e) {
				// doesn't matter
			}

		}
		return;

	}

}
