package org.matsim.webvis.frameAnimation.data;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAuthorizedException;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataController {

    public static final DataController Instance = new DataController();

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static boolean isFetchingNewData = false;

    private Instant lastFetch = Instant.MIN;

    private DataController() {
    }

    public void scheduleFetching() {

        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 10, TimeUnit.HOURS);
    }

    void fetchVisualizations() {

        if (isFetchingNewData) {
            logger.info("already fetching data. Wait until operation has finished.");
        } else {
            logger.info("scheduling single fetch.");
            scheduler.schedule(this::fetchVisualizationData, 0, TimeUnit.SECONDS);
        }
    }

    private void fetchVisualizationData() {

        isFetchingNewData = true;
        Instant requestTime = Instant.now();
        URI vizByTypeEndpoint = AppConfiguration.getInstance().getFileServer().resolve("/visualizations");
        try {
            Visualization[] response = ServiceCommunication.getClient().target(vizByTypeEndpoint)
                    .queryParam("type", "Animation")
                    .queryParam("after", lastFetch.toString())
                    .request()
                    .property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, ServiceCommunication.getAuthentication().getAccessToken())
                    .get(Visualization[].class);

            logger.info("Received " + response.length + " vizes.");

            lastFetch = requestTime;
            isFetchingNewData = false;

            for (Visualization viz : response)
                SimulationDataFetcher.generateVisualization(viz);

        } catch (NotAuthorizedException e) {
            logger.info("could not authenticate attempting to refresh access_token");
            ServiceCommunication.getAuthentication().requestAccessToken();
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata from: " + vizByTypeEndpoint, e);
        } finally {
            isFetchingNewData = false;
        }
    }
}
