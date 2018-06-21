package org.matsim.webvis.frameAnimation;

import org.matsim.webvis.frameAnimation.constants.Path;
import org.matsim.webvis.frameAnimation.requestHandling.ConfigurationRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.NetworkRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.PlanRequestHandler;
import org.matsim.webvis.frameAnimation.requestHandling.SnapshotRequestHandler;

import static spark.Spark.post;

class Routes {

    static void initialize() {

        post(Path.CONFIGURATION, new ConfigurationRequestHandler());
        post(Path.NETWORK, new NetworkRequestHandler());
        post(Path.SNAPSHOTS, new SnapshotRequestHandler());
        post(Path.PLAN, new PlanRequestHandler());
    }
}
