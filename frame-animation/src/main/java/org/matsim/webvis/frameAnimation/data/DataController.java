package org.matsim.webvis.frameAnimation.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class DataController {

    static final DataController Instance = new DataController();

    private static final Logger logger = LogManager.getLogger();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Http http = ServiceCommunication.http();
    private ClientAuthentication authentication = ServiceCommunication.authentication();
    private MetadataDAO metadataDAO = new MetadataDAO();

    private DataController() {
    }

    void scheduleHourlyFetching() {

        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 1, TimeUnit.HOURS);
    }

    private void fetchVisualizationData() {

        URI vizByTypeEndpoint = Configuration.getInstance().getFileServer().resolve("/project/visualizations/");

        try {
            Visualization[] response = http.post(vizByTypeEndpoint)
                    .withCredential(() -> authentication.headerValue())
                    .withJsonBody(new VizTypeRequest())
                    .executeWithJsonResponse(Visualization[].class);

            logger.info("Received " + response.length + " vizes.");

            metadataDAO.persistVisualizations(Arrays.asList(response));
        } catch (UnauthorizedException e) {
            ServiceCommunication.authentication().requestAccessToken();
        } catch (CodedException e) {
            logger.error("Error while fetching viz metadata.", e);
        }
    }

    private static class VizTypeRequest {
        final String visualizationType = "Animation";
    }
}
