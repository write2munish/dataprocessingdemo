package org.demo.data.processing.actor;

import org.demo.data.processing.messages.MockData;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


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
