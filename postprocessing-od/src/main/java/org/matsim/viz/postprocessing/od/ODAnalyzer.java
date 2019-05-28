package org.matsim.viz.postprocessing.od;

import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.utils.geometry.geotools.MGC;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@AllArgsConstructor
class ODAnalyzer {

    private final Path eventFile;
    private final Network network;
    private final FeatureCollection odZones;

    Map<String, ODRelation> run() {

        EventsManager manager = EventsUtils.createEventsManager();
        TripEventHandler handler = new TripEventHandler();
        manager.addHandler(handler);
        new MatsimEventsReader(manager).readFile(eventFile.toString());

        System.out.println("starting computation");
        Instant start = Instant.now();
        Map<String, ODRelation> result = handler.getTripToPerson().values().parallelStream()
                .flatMap(Collection::parallelStream)
                .map(trip -> {
                    Coord from = network.getLinks().get(trip.getDepartureLink()).getCoord();
                    Coord to = network.getLinks().get(trip.getArrivalLink()).getCoord();
                    return new ODMeasure(trip.getMainMode(), getZoneIndex(from), getZoneIndex(to));
                })
                .filter(ODMeasure::hasValidToAndFromZone)
                .collect(Collectors.groupingBy(ODMeasure::getFromToKey,
                        Collector.of(ODRelation::new, ODRelation::accept, ODRelation::combine)));

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("finished computation. It took: " + duration.toString());
        return result;
    }

    private int getZoneIndex(Coord coord) {

        Point point = MGC.coord2Point(coord);
        for (int i = 0; i < odZones.getFeatures().size(); i++) {
            if (odZones.getFeatures().get(i).getGeometry().contains(point))
                return i;
        }
        return -1; // if not contained we return an impossible index
    }
}
