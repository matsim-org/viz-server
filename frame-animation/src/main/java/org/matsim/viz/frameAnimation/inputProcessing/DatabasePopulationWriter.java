package org.matsim.viz.frameAnimation.inputProcessing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.geojson.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.viz.frameAnimation.persistenceModel.Plan;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class DatabasePopulationWriter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path population;
    private final Network network;
    private final SessionFactory sessionFactory;
    private final Visualization visualization;

    void readPopulationAndWriteToDatabase() {

        val scenario = ScenarioUtils.createMutableScenario(ConfigUtils.createConfig());
        scenario.setNetwork(this.network);
        val reader = new PopulationReader(scenario);
        reader.readFile(population.toString());
        readPopulationAndWriteToDatabase(scenario.getPopulation());
    }

    private void readPopulationAndWriteToDatabase(Population population) {

        try (val session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                population.getPersons().values().stream()
                        .map(this::planToFeatureCollection)
                        .forEach(features -> writeToDatabase(features, session));

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
    }

    private void writeToDatabase(FeatureCollection features, Session session) {

        try {
            val plan = new Plan();
            visualization.addPlan(plan);
            plan.setGeoJson(objectMapper.writeValueAsString(features));
            session.save(plan);
            session.flush();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not write JSON");
        }
    }

    private FeatureCollection planToFeatureCollection(Person person) {

        val featureCollection = new FeatureCollection();
        person.getSelectedPlan().getPlanElements().forEach(element -> {
            if (element instanceof Leg) {
                val leg = (Leg) element;
                if (leg.getRoute() instanceof NetworkRoute) // anything else will not work, just ignore it
                    featureCollection.add(createLegFeature((NetworkRoute) leg.getRoute()));
            } else if (element instanceof Activity) {
                val activity = (Activity) element;
                if (activity.getCoord() != null)
                    featureCollection.add(createActivityFeature(activity.getCoord()));
            }
        });
        return featureCollection;
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
