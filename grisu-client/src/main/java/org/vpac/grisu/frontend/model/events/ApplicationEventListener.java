package org.vpac.grisu.frontend.model.events;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.vpac.grisu.model.status.ActionStatusEvent;

public class ApplicationEventListener implements EventSubscriber {

	public ApplicationEventListener() {
		EventBus.subscribe(ActionStatusEvent.class, this);
		EventBus.subscribe(BatchJobEvent.class, this);
		EventBus.subscribeStrongly(BatchJobKilledEvent.class, this);
		EventBus.subscribe(JobStatusEvent.class, this);
	}

	public void onEvent(Object event) {
		System.out.println("EVENT: " + event.toString());
	}

}
