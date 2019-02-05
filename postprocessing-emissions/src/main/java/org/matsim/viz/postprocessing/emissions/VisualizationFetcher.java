package org.matsim.viz.postprocessing.emissions;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Visualization;
import org.matsim.viz.postprocessing.emissions.persistenceModel.QFetchInformation;

import javax.persistence.EntityManager;
import java.time.Instant;

@Log
@RequiredArgsConstructor
class VisualizationFetcher {

    private final SessionFactory sessionFactory;
    private final FilesApi api;
    private final VisualizationGeneratorFactory generatorFactory;

    private boolean isFetching = false;

    void fetchVisualizationData() {

        isFetching = true;

        try (val session = sessionFactory.openSession()) {

            session.beginTransaction();

            val fetchInformation = getFetchInformation(session);
            val requestTime = Instant.now();

            Visualization[] response = api.fetchVisualizations("emissions", fetchInformation.getLastFetch());

            fetchInformation.setLastFetch(requestTime);
            session.getTransaction().commit();
            isFetching = false;

            log.info("received data for " + response.length + " visualizations");
            log.info("start processing metadata");

            for (Visualization visualization : response) {
                val generator = generatorFactory.createGenerator(visualization);
                generator.generate();
            }
        } finally {
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
