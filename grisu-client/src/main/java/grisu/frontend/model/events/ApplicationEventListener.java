package grisu.frontend.model.events;

import grisu.model.status.ActionStatusEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

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
