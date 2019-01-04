package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VisualizationFetcher {

    public static final Logger logger = LoggerFactory.getLogger(VisualizationFetcher.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    private final FilesAPI filesAPI;
    private final VisualizationGeneratorFactory generatorFactory;

    private Instant lastFetch = Instant.MIN;
    private boolean isFetching = false;


    public void scheduleFetching() {
        scheduler.scheduleAtFixedRate(this::fetchVisualizationData, 0, 12, TimeUnit.HOURS);
    }

    public void fetchVisualizations() {

        if (isFetching) {
            logger.info("already fetching data. Wait until operation has finished.");
        } else {
            logger.info("scheduling single fetch.");
            scheduler.schedule(this::fetchVisualizationData, 0, TimeUnit.SECONDS);
        }
    }

    void fetchVisualizationData() {

        isFetching = true;
        Instant requestTime = Instant.now();
        try {
            Visualization[] response = filesAPI.fetchVisualizations(lastFetch);

            lastFetch = requestTime;
            isFetching = false;

            logger.info("Received metadata for " + response.length + " visualizations");

            for (Visualization viz : response) {
                val generator = generatorFactory.create(viz);
                scheduler.schedule(generator::generate, 0, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata from: ", e);
        } finally {
            isFetching = false;
        }
    }

}
