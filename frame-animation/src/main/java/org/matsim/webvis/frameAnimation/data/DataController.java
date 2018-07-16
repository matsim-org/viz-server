package org.matsim.webvis.frameAnimation.data;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataController {

    public static final DataController Instance = new DataController();

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private DataController() {
    }

    public void scheduleFetching() {

        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 10, TimeUnit.HOURS);
    }

    private void fetchVisualizationData() {

        URI vizByTypeEndpoint = AppConfiguration.getInstance().getFileServer().resolve("/visualizations");

        try {
            Visualization[] response = ServiceCommunication.getClient().target(vizByTypeEndpoint)
                    .queryParam("type", "Animation")
                    .request()
                    .property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, ServiceCommunication.getAuthentication().getAccessToken())
                    .get(Visualization[].class);

            logger.info("Received " + response.length + " vizes.");

            for (Visualization viz : response)
                SimulationDataFetcher.generateVisualization(viz);

        } catch (UnauthorizedException e) {
            logger.info("could not authenticate attempting to refresh access_token");
            ServiceCommunication.getAuthentication().requestAccessToken();
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata.", e);
        }
    }

    private static class VizTypeRequest {
        final String visualizationType = "Animation";
    }
}
