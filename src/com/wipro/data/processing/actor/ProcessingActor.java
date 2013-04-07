package com.wipro.data.processing.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.wipro.data.processing.messages.MockData;

public class ProcessingActor extends UntypedActor {

	ActorRef updateDbActor;

	public ProcessingActor(ActorRef updateDB) {
		updateDbActor = updateDB;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof MockData) {
			MockData data = (MockData) msg;

			data.setTimesProcessed(data.getTimesProcessed() + 1);
			data.setCalculatedValue(data.getCalculatedValue() + 10);
			// run a dummy loop
			for (int i = 0; i < 2000000; i++)
				;

			updateDbActor.tell(data);
		}

	}

}
