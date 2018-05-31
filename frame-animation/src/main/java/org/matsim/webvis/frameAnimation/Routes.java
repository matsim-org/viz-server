package org.matsim.webvis.frameAnimation;

import org.matsim.webvis.frameAnimation.constants.Path;
import org.matsim.webvis.frameAnimation.data.SimulationData;
import org.matsim.webvis.frameAnimation.requestHandling.ConfigurationRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.NetworkRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.PlanRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.SnapshotRequestHandler;

import static spark.Spark.port;
import static spark.Spark.post;

class Routes {

    static void initialize(SimulationData data) {

        post(Path.CONFIGURATION, new ConfigurationRequestHandler(data));
        post(Path.NETWORK, new NetworkRequestHandler(data));
        post(Path.SNAPSHOTS, new SnapshotRequestHandler(data));
        post(Path.PLAN, new PlanRequestHandler(data));
    }
}
