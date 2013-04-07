package org.demo.data.processing.actor;

import org.demo.data.processing.messages.InstrumentationMsg;

import akka.actor.UntypedActor;


public class InstrumentationActor extends UntypedActor {

	long startTime;
	long endTime;

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof InstrumentationMsg) {
			InstrumentationMsg value = (InstrumentationMsg) msg;
			if (value.getType() == 1) {
				startTime = value.getTime();
			} else if (value.getType() == 2) {
				if (endTime < value.getTime()) {
					endTime = value.getTime();
				}
			}
		} else if (msg instanceof String) {
			long timeTaken = endTime - startTime;
			System.out.println("Time taken to process(in millisecs) -> " + timeTaken);
		}

	}

}
