package org.matsim.viz.postprocessing.od;

import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;

public class EventHandler implements TransitDriverStartsEventHandler, ActivityEndEventHandler, PersonDepartureEventHandler, PersonArrivalEventHandler {
	@Override
	public void handleEvent(PersonArrivalEvent event) {

	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {

	}

	@Override
	public void handleEvent(TransitDriverStartsEvent event) {

	}

	@Override
	public void handleEvent(ActivityEndEvent event) {

	}
}
