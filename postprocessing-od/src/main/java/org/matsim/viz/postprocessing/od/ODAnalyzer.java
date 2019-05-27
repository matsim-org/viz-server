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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
class ODAnalyzer {

    private final Path eventFile;
    private final Network network;
    private final FeatureCollection odZones;

    Map<String, Map<String, Integer>> run() {

        EventsManager manager = EventsUtils.createEventsManager();
        EventHandler handler = new EventHandler();
        manager.addHandler(handler);
        new MatsimEventsReader(manager).readFile(eventFile.toString());

        Map<String, Map<String, Integer>> numberOfTripsByOdAndMode = new HashMap<>();

        System.out.println("starting computation");
        Instant start = Instant.now();
        Map<String, Map<String, Integer>> test = handler.getTripToPerson().values().parallelStream()
                .flatMap(Collection::parallelStream)
                .map(trip -> {
                    Coord from = network.getLinks().get(trip.getDepartureLink()).getCoord();
                    Coord to = network.getLinks().get(trip.getArrivalLink()).getCoord();
                    return new ODMeasure(trip.getMainMode(), createODKey(from, to));
                }).collect(Collectors.groupingBy(ODMeasure::getKey,
                        Collectors.groupingBy(ODMeasure::getMode, Collectors.summingInt(measure -> 1))));

        Duration duration = Duration.between(start, Instant.now());
        System.out.println("finished computation. It took: " + duration.toString());
        return numberOfTripsByOdAndMode;
    }

    private String createODKey(Coord from, Coord to) {
        return getZoneIndex(from) + "_" + getZoneIndex(to);
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
