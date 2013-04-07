package com.wipro.data.processing.actor;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Duration;

import com.wipro.data.processing.messages.FetchData;

public class SchedulerActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef loadDataActor;

	Integer totalNumberofRows;
	int noOfRows = 2000;
	int lastCount = 0;

	public SchedulerActor(ActorRef loadData) {
		loadDataActor = loadData;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Integer) {
			totalNumberofRows = (Integer) msg;

			getContext()
					.system()
					.scheduler()
					.scheduleOnce(Duration.create(20, TimeUnit.MILLISECONDS),
							getSelf(), "Next");

		} else if (msg instanceof String) {

			if (lastCount < totalNumberofRows) {

				loadDataActor.tell(new FetchData(lastCount, noOfRows));

				lastCount += noOfRows;

				getContext()
						.system()
						.scheduler()
						.scheduleOnce(
								Duration.create(20, TimeUnit.MILLISECONDS),
								getSelf(), "Next");
			}
		}

	}

}
