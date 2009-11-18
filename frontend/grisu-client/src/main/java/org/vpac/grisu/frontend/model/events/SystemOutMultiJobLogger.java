package org.vpac.grisu.frontend.model.events;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

public class SystemOutMultiJobLogger implements EventTopicSubscriber<MultiPartJobEvent> {
	
	public SystemOutMultiJobLogger(String topic) {
		EventBus.subscribe(topic, this);
	}

	public void onEvent(String arg0, MultiPartJobEvent arg1) {
		
		System.out.println("Multipartjob "+arg0+": "+arg1.getMessage());
		
	}


	
	
}
