package org.matsim.webvis.frameAnimation.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataController {

    public static final DataController Instance = new DataController();

    private static final Logger logger = LogManager.getLogger();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private MetadataDAO metadataDAO = new MetadataDAO();

    private DataController() {
    }

    public void scheduleFetching() {

        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 10, TimeUnit.HOURS);
    }

    private void fetchVisualizationData() {

        URI vizByTypeEndpoint = Configuration.getInstance().getFileServer().resolve("/project/visualizations/");

        try {
            Visualization[] response = ServiceCommunication.http().post(vizByTypeEndpoint)
                    .withCredential(ServiceCommunication.authentication())
                    .withJsonBody(new VizTypeRequest())
                    .executeWithJsonResponse(Visualization[].class);

            logger.info("Received " + response.length + " vizes.");

            metadataDAO.persistVisualizations(Arrays.asList(response));
            for (Visualization viz : response)
                SimulationDataFetcher.generateVisualization(viz);

        } catch (UnauthorizedException e) {
            logger.info("could not authenticate attempting to refresh access_token");
            ServiceCommunication.authentication().requestAccessToken();
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata.", e);
        }
    }

    private static class VizTypeRequest {
        final String visualizationType = "Animation";
    }
}
