package data;

import contracts.geoJSON.FeatureCollection;
import contracts.geoJSON.Geometry;
import contracts.geoJSON.LineString;
import contracts.geoJSON.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.routes.NetworkRoute;

import java.util.Collection;

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
                Geometry geometry = createLine((NetworkRoute) leg.getRoute());
                result.addFeature(geometry, "leg");
            }
            if (element instanceof Activity) {
                Activity activity = (Activity) element;
                if (activity.getCoord() != null) {
                    Geometry geometry = createPoint(activity.getCoord());
                    result.addFeature(geometry, "activity");
                }
            }
        }
        return result;
    }

    private Geometry createLine(NetworkRoute route) {

        Geometry result = new LineString();

        for (Id id : route.getLinkIds()) {
            Link link = network.getLinks().get(id);
            result.addCoordinate(link.getFromNode().getCoord());
            result.addCoordinate(link.getToNode().getCoord());
        }

        return result;
    }

    private Geometry createPoint(Coord coord) {

        return new Point(coord);
    }
}