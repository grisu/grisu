package org.vpac.grisu.frontend.model.events;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

public class SystemOutStatusLogger implements
		EventTopicSubscriber<ActionStatusEvent> {

	public SystemOutStatusLogger(String topic) {
		EventBus.subscribe(topic, this);
	}

	public void onEvent(String arg0, ActionStatusEvent arg1) {

		System.out.println(arg1.getPercentFinished() + "% finished...");

	}

}
