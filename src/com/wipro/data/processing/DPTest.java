package com.wipro.data.processing;

import java.util.concurrent.TimeUnit;

import com.wipro.data.processing.actor.InstrumentationActor;
import com.wipro.data.processing.actor.LoadDataActor;
import com.wipro.data.processing.actor.ProcessingActor;
import com.wipro.data.processing.actor.SchedulerActor;
import com.wipro.data.processing.actor.UpdateDBActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;

public class DPTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final ActorSystem _system = ActorSystem.create("DataProcessingDemo");

		final ActorRef instrumentationActor = _system.actorOf(new Props(
				InstrumentationActor.class));
		final ActorRef updateDBActor = _system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new UpdateDBActor(instrumentationActor);
					}
				}).withRouter(new RoundRobinRouter(4)));

		final ActorRef processingActor = _system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new ProcessingActor(updateDBActor);
					}
				}).withRouter(new RoundRobinRouter(10)));

		final ActorRef loadDataActor = _system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new LoadDataActor(processingActor,
								instrumentationActor);
					}
				}));

		ActorRef schedulerActor = _system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new SchedulerActor(loadDataActor);
					}
				}));

		schedulerActor.tell(Integer.parseInt("100000"));

		schedulerActor.tell("Start");

		Thread.sleep(10000);

		instrumentationActor.tell("updateall");

		_system.shutdown();

	}

}
