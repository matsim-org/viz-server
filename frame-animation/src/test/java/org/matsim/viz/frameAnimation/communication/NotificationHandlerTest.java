package org.matsim.viz.frameAnimation.communication;

import lombok.val;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.frameAnimation.inputProcessing.VisualizationFetcher;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.matsim.viz.frameAnimation.utils.DatabaseTest;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.net.URI;

import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationHandlerTest extends DatabaseTest {

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadConfig();
    }

    @Test
    public void notification_visualizationCreated() {

        val visualizationFetcher = mock(VisualizationFetcher.class);
        final String id = "some-id";
        NotificationHandler.Notification notification = new NotificationHandler.Notification("visualization_created", id);
        NotificationHandler handler = new NotificationHandler(visualizationFetcher, URI.create("http://some.uri"), database.getSessionFactory());

        handler.visualizationCallback(notification);

        verify(visualizationFetcher).fetchVisualizations();
    }

    @Test
    public void notification_visualizationDeleted() {

        // store a viz to remove
        Visualization visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            toPersist.setId("some-id");
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });

        val visualizationFetcher = mock(VisualizationFetcher.class);
        NotificationHandler.Notification notification = new NotificationHandler.Notification("visualization_deleted", visualization.getId());
        NotificationHandler handler = new NotificationHandler(visualizationFetcher, URI.create("http://some.uri"), database.getSessionFactory());

        handler.visualizationCallback(notification);

        // open a new session to force a reload of the visualization from the db
        try (val session = database.getSessionFactory().openSession()) {
            Visualization viz = session.find(Visualization.class, visualization.getId());
            assertNull(viz);
        }
    }

    @Test(expected = InternalException.class)
    public void notification_unknownType() {

        NotificationHandler.Notification notification = new NotificationHandler.Notification("unknown_type", "some-id");
        NotificationHandler handler = new NotificationHandler(mock(VisualizationFetcher.class), URI.create("http://some.uri"), database.getSessionFactory());

        handler.visualizationCallback(notification);
    }
}
