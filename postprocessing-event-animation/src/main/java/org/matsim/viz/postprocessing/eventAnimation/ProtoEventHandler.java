package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ProtoEventHandler implements LinkEnterEventHandler, LinkLeaveEventHandler {

    private final Network network;
    private Map<String, LinkTripProto.LinkTrip.Builder> builderMap = new HashMap<>();

    @Getter
    private Set<LinkTripProto.LinkTrip> result = new HashSet<>();

    @Override
    public void handleEvent(LinkEnterEvent linkEnterEvent) {
        Coord fromNode = network.getLinks().get(linkEnterEvent.getLinkId()).getFromNode().getCoord();
        LinkTripProto.LinkTrip.Builder linkTrip = LinkTripProto.LinkTrip.newBuilder()
                .setEnterTime((float) linkEnterEvent.getTime())
                .setFromX((float) fromNode.getX())
                .setFromY((float) fromNode.getY());
        builderMap.put(getLinkTripsKey(linkEnterEvent.getVehicleId(), linkEnterEvent.getLinkId()), linkTrip);
    }

    @Override
    public void handleEvent(LinkLeaveEvent linkLeaveEvent) {
        String key = getLinkTripsKey(linkLeaveEvent.getVehicleId(), linkLeaveEvent.getLinkId());
        if (builderMap.containsKey(key)) {


            Coord toNode = network.getLinks().get(linkLeaveEvent.getLinkId()).getToNode().getCoord();
            LinkTripProto.LinkTrip.Builder builder = builderMap.remove(key);
            LinkTripProto.LinkTrip trip = builder.setLeaveTime((float) linkLeaveEvent.getTime())
                    .setToX((float) toNode.getX())
                    .setToY((float) toNode.getY())
                    .build();

            result.add(trip);
        }
    }

    private String getLinkTripsKey(Id<Vehicle> vehicleId, Id<Link> linkId) {
        return vehicleId.toString() + linkId.toString();
    }
}
