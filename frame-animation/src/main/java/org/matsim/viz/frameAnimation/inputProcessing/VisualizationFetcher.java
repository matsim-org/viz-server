package org.matsim.viz.frameAnimation.inputProcessing;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.matsim.viz.frameAnimation.persistenceModel.FetchInformation;
import org.matsim.viz.frameAnimation.persistenceModel.QFetchInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
    private final EntityManagerFactory emFactory;

    //private Instant lastFetch = Instant.MIN;
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

        val em = emFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            // there hopefully will be one fetch info at all times
            val fetchInformation = getFetchInformation(em);
            Instant requestTime = Instant.now();

            Visualization[] response = filesAPI.fetchVisualizations(fetchInformation.getLastFetch());

            fetchInformation.setLastFetch(requestTime);
            em.getTransaction().commit();
            isFetching = false;

            logger.info("Received metadata for " + response.length + " visualizations");

            for (Visualization viz : response) {
                val generator = generatorFactory.create(viz);
                scheduler.schedule(generator::generate, 0, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("Error while fetching viz metadata from: ", e);
        } finally {
            em.close();
            isFetching = false;
        }
    }

    private FetchInformation getFetchInformation(EntityManager em) {

        FetchInformation fetchInformation = new JPAQueryFactory(em).selectFrom(QFetchInformation.fetchInformation).fetchFirst();
        if (fetchInformation == null) {
            fetchInformation = new FetchInformation();
            em.persist(fetchInformation);
        }
        return fetchInformation;
    }
}
