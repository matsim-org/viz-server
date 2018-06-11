package org.matsim.webvis.frameAnimation.data;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DataController {

    public static final DataController Instance = new DataController();

    private static final Logger logger = LogManager.getLogger();

    private Http http = ServiceCommunication.http();
    private ClientAuthentication authentication = ServiceCommunication.authentication();
    private Map<String, Visualization> store = new HashMap<>();

    private DataController() {

    }

    public void fetchVisualizationData() {

        URI vizByTypeEndpoint = Configuration.getInstance().getFileServer().resolve("/project/visualizations/");
        URI vizEndpoint = Configuration.getInstance().getFileServer().resolve("/project/visualization/");

        Visualization[] response = http.post(vizByTypeEndpoint)
                .withCredential(() -> authentication.headerValue())
                .withJsonBody(new VizTypeRequest())
                .executeWithJsonResponse(Visualization[].class);

        logger.info("Received " + response.length + " vizes.");

        for (Visualization viz : response) {
            store.put(viz.getId(), viz);
        }

    }

    private static class VizTypeRequest {
        final String visualizationType = "Animation";
    }

    @AllArgsConstructor
    private static class VizRequest {
        String visualizationId;
    }


}
