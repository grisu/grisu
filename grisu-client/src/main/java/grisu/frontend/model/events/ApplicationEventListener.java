package grisu.frontend.model.events;

import grisu.model.status.ActionStatusEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationEventListener implements EventSubscriber {
	
	public static final Logger myLogger = LoggerFactory.getLogger(ApplicationEventListener.class);

	public ApplicationEventListener() {
		EventBus.subscribe(ActionStatusEvent.class, this);
		EventBus.subscribe(BatchJobEvent.class, this);
		EventBus.subscribeStrongly(BatchJobKilledEvent.class, this);
		EventBus.subscribe(JobStatusEvent.class, this);
	}

	public void onEvent(Object event) {
		myLogger.debug("EVENT: " + event.toString());
	}

}
