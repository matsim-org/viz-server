package org.matsim.viz.files.notifications;

import org.junit.Test;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.ServiceAgent;
import org.matsim.viz.files.entities.User;

import java.net.URI;
import java.time.Instant;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationResourceTest {

    @Test(expected = UnauthorizedException.class)
    public void subscribe_invalidAuth_exception() {

        Agent invalidAuthAgent = new User();
        NotificationResource.SubscriptionRequest request = new NotificationResource.SubscriptionRequest(
                "some-type", URI.create("http://some-callback.com")
        );
        Notifier notifier = mock(Notifier.class);
        NotificationResource testObject = new NotificationResource(notifier);

        testObject.subscribe(invalidAuthAgent, request);

        fail("invalid agent type should cause exception");
    }

    @Test
    public void subscribe_subscriptionCreated_subscription() {

        Agent authAgent = new ServiceAgent();
        final String type = "some-type";
        final URI callback = URI.create("http://some-callback.com");
        Subscription expectedResult = new Subscription(new NotificationType(type), callback, Instant.now());
        NotificationResource.SubscriptionRequest request = new NotificationResource.SubscriptionRequest(
                type, callback
        );
        Notifier notifier = mock(Notifier.class);
        when(notifier.createSubscription(any(), any())).thenReturn(expectedResult);
        NotificationResource testObject = new NotificationResource(notifier);

        Subscription result = testObject.subscribe(authAgent, request);

        assertEquals(expectedResult, result);
    }
}
