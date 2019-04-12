package org.matsim.viz.postprocessing.bundle;

import org.junit.Ignore;
import org.junit.Test;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Subscription;

import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NotificationResourceTest {

    @Test
    public void init_registerNotificationIsCalled() {

        final Subscription expectedResult = new Subscription(new Subscription.NotificationType("some-type"),
                URI.create("http://some.uri"), Instant.now().plus(Duration.ofDays(1)));
        FilesApi api = mock(FilesApi.class);
        when(api.registerNotification(anyString(), any())).thenReturn(expectedResult);

        NotificationResource resource = new NotificationResource(api, URI.create("http://some.uri"), mock(VisualizationFetcher.class));

        assertNotNull(resource);
        verify(api, timeout(100).times(1)).registerNotification(any(), any());
    }

    @Test
    public void init_registerNotificationIsCalledSeveralTimesOnFailure() {

        final Subscription expectedResult = new Subscription(new Subscription.NotificationType("some-type"),
                URI.create("http://some.uri"), Instant.now().plus(Duration.ofDays(1)));
        FilesApi api = mock(FilesApi.class);
        WebApplicationException exception = mock(WebApplicationException.class);
        when(api.registerNotification(anyString(), any())).thenThrow(exception).thenReturn(expectedResult);

        NotificationResource resource = new NotificationResource(api, URI.create("http://some.uri"), mock(VisualizationFetcher.class));

        assertNotNull(resource);
        verify(api, timeout(1300).times(2)).registerNotification(any(), any());
    }

    @Test
    @Ignore
    public void callback_notYetImplemented() {
        fail("implement test");
    }
}
