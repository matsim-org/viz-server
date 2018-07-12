package org.matsim.webvis.frameAnimation.data;

import org.geojson.*;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.routes.NetworkRoute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class PopulationData {

    private Population population;
    private Network network;

    PopulationData(Population population, Network network) {

        this.population = population;
        this.network = network;
    }

    FeatureCollection getSelectedPlan(Id id) {
        Plan plan = population.getPersons().get(id).getSelectedPlan();
        return createFeatureCollection(plan.getPlanElements());
    }

    private FeatureCollection createFeatureCollection(Collection<PlanElement> elements) {

        FeatureCollection result = new FeatureCollection();
        for (PlanElement element : elements) {
            if (element instanceof Leg) {
                Leg leg = (Leg) element;
                Feature feature = createLegFeature((NetworkRoute) leg.getRoute());
                result.add(feature);
            }
            if (element instanceof Activity) {
                Activity activity = (Activity) element;
                result.add(createActivityFeature(activity.getCoord()));
            }
        }
        return result;
    }

    private Feature createLegFeature(NetworkRoute route) {

        Feature feature = new Feature();
        feature.setProperty("type", "leg");
        List<LngLatAlt> points = new ArrayList<>();
        for (Id id : route.getLinkIds()) {

            Link link = network.getLinks().get(id);
            points.add(new LngLatAlt(link.getFromNode().getCoord().getX(), link.getFromNode().getCoord().getY()));
            points.add(new LngLatAlt(link.getToNode().getCoord().getX(), link.getToNode().getCoord().getY()));
        }

        feature.setGeometry(new LineString(points.toArray(new LngLatAlt[0])));
        return feature;
    }

    private Feature createActivityFeature(Coord coord) {

        Feature feature = new Feature();
        feature.setProperty("type", "activity");
        feature.setGeometry(new Point(new LngLatAlt(coord.getX(), coord.getY())));
        return feature;
    }
}