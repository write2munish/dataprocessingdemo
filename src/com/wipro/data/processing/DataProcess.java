package com.wipro.data.processing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;
import akka.routing.RoundRobinRouter;

import com.wipro.data.processing.actor.InstrumentationActor;
import com.wipro.data.processing.actor.LoadDataActor;
import com.wipro.data.processing.actor.ProcessingActor;
import com.wipro.data.processing.actor.SchedulerActor;
import com.wipro.data.processing.actor.UpdateDBActor;

public class DataProcess implements Bootable {

	final ActorSystem _system = ActorSystem.create("DataProcessingDemo");
	final ActorRef instrumentationActor = _system.actorOf(new Props(
			InstrumentationActor.class));
	final ActorRef updateDBActor = _system.actorOf(new Props(
			new UntypedActorFactory() {
				public UntypedActor create() {
					return new UpdateDBActor(instrumentationActor);
				}
			}).withRouter(new RoundRobinRouter(5)));
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

	final ActorRef schedulerActor = _system.actorOf(new Props(
			new UntypedActorFactory() {
				public UntypedActor create() {
					return new SchedulerActor(loadDataActor);
				}
			}));

	@Override
	public void startup() {

		schedulerActor.tell(Integer.parseInt("100000"));

	}

	@Override
	public void shutdown() {
		instrumentationActor.tell("Time");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		_system.shutdown();

	}

}
