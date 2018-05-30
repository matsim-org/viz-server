package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.InternalException;
import org.matsim.webvis.common.util.TestUtils;

import java.io.IOException;
import java.net.URI;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestTest {

    @Test
    public void withJsonResponse_statusNotOkButErrorResponse_codedException() {

        ErrorResponse error = new ErrorResponse("unauthorized", "you are unauthorized");
        HttpClientFactory factory = TestUtils.mockHttpClientFactory(401, new Gson().toJson(error));
        URI endpoint = URI.create("http://some-uri.com");
        HttpPost post = new HttpPost(endpoint);

        try {
            HttpRequest.withJsonResponse(post, factory, Object.class);
        } catch (CodedException e) {
            assertEquals(error.getError(), e.getErrorCode());
            assertEquals(error.getError_description(), e.getMessage());
        }
    }

    @Test(expected = InternalException.class)
    public void withJsonResponse_statusNotOkUnknownResponse_internalException() {

        HttpClientFactory factory = TestUtils.mockHttpClientFactory(401, "error message");
        URI endpoint = URI.create("http://some-uri.com");
        HttpPost post = new HttpPost(endpoint);

        HttpRequest.withJsonResponse(post, factory, Object.class);

        fail("unknown error message should cause internal exception");
    }

    @Test(expected = InternalException.class)
    public void withJsonResponse_errorDuringRequestExecution_internalException() throws IOException {

        CloseableHttpClient client = mock(CloseableHttpClient.class);
        when(client.execute(any())).thenThrow(new IOException());

        HttpClientFactory factory = mock(HttpClientFactory.class);
        when(factory.createClient()).thenReturn(client);

        URI endpoint = URI.create("http://some-uri.com");
        HttpPost post = new HttpPost(endpoint);

        HttpRequest.withJsonResponse(post, factory, Object.class);

        fail("unknown error message should cause internal exception");
    }

    @Test
    public void withJsonResponse_statusOk_ResponseObject() {

        Response expectedResponse = new Response("value");
        HttpClientFactory factory = TestUtils.mockHttpClientFactory(200, new Gson().toJson(expectedResponse));
        URI endpoint = URI.create("http://some-uri.com");
        HttpPost post = new HttpPost(endpoint);

        Response response = HttpRequest.withJsonResponse(post, factory, Response.class);

        assertEquals(expectedResponse.property, response.property);
    }

    @AllArgsConstructor
    private static class Response {
        String property;
    }
}
