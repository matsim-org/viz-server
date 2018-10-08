package org.matsim.webvis.frameAnimation.communication;

import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.frameAnimation.data.DataController;
import org.matsim.webvis.frameAnimation.data.DataProvider;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import java.net.URI;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationHandlerTest {

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadConfig();
    }

    @Test
    public void notification_visualizationCreated() {

        final DataController dataController = mock(DataController.class);
        final String id = "some-id";
        NotificationHandler.Notification notification = new NotificationHandler.Notification("visualization_created", id);
        NotificationHandler handler = new NotificationHandler(dataController, mock(DataProvider.class), URI.create("http://some.uri"));

        handler.visualizationCallback(notification);

        verify(dataController).fetchVisualizations();
    }

    @Test
    public void notification_visualizationDeleted() {

        final DataProvider dataProvider = mock(DataProvider.class);
        final String id = "some-id";
        NotificationHandler.Notification notification = new NotificationHandler.Notification("visualization_deleted", id);
        NotificationHandler handler = new NotificationHandler(mock(DataController.class), dataProvider, URI.create("http://some.uri"));

        handler.visualizationCallback(notification);

        verify(dataProvider).remove(eq(id));
    }

    @Test(expected = InternalException.class)
    public void notification_unknownType() {

        NotificationHandler.Notification notification = new NotificationHandler.Notification("unknown_type", "some-id");
        NotificationHandler handler = new NotificationHandler(mock(DataController.class), mock(DataProvider.class), URI.create("http://some.uri"));

        handler.visualizationCallback(notification);
    }
}
