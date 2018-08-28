package org.matsim.webvis.frameAnimation.data;

import org.matsim.webvis.frameAnimation.communication.FilesAPI;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataController {

    public static final DataController Instance = new DataController();
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    private Instant lastFetch = Instant.MIN;
    FilesAPI filesAPI = FilesAPI.Instance;
    DataGeneratorFactory dataGeneratorFactory = new DataGeneratorFactory();
    private boolean isFetchingNewData = false;

    DataController() {
    }

    public void scheduleFetching() {

        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 10, TimeUnit.HOURS);
    }

    public void fetchVisualizations() {

        if (isFetchingNewData) {
            logger.info("already fetching data. Wait until operation has finished.");
        } else {
            logger.info("scheduling single fetch.");
            scheduler.schedule(this::fetchVisualizationData, 0, TimeUnit.SECONDS);
        }
    }

    void fetchVisualizationData() {

        isFetchingNewData = true;
        Instant requestTime = Instant.now();
        try {
            Visualization[] response = filesAPI.fetchVisualizations(lastFetch);

            lastFetch = requestTime;
            isFetchingNewData = false;

            logger.info("Received metadata for " + response.length + " visualizations");

            for (Visualization viz : response) {
                DataGenerator generator = dataGeneratorFactory.createGenerator(viz);
                scheduler.schedule(generator::generate, 0, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata from: ", e);
        } finally {
            isFetchingNewData = false;
        }
    }
}
