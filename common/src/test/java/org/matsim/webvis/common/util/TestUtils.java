package org.matsim.webvis.common.util;

import org.apache.http.Consts;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.communication.HttpClientFactoryWithTruststore;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static String encodeBasicAuth(String principal, String credential) {
        return "Basic " + Base64.getEncoder().encodeToString((principal + ":" + credential).getBytes());
    }

    public static Path getKeystorePath() {
        return Paths.get(getResourcePath("keystore.jks"));
    }

    public static char[] getKeystorePassword() {
        return "chocopause".toCharArray();
    }

    @SuppressWarnings("ConstantConditions")
    private static URI getResourcePath(String resource) {
        try {
            return TestUtils.class.getClassLoader().getResource(resource).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("could not load resource: " + resource, e);
        }

    }

    public static CloseableHttpResponse mockHttpResponse(int status, String content) {

        CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);

        StatusLine mockedStatusLine = mock(StatusLine.class);
        when(mockedStatusLine.getStatusCode()).thenReturn(status);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);

        when(mockedResponse.getEntity()).thenReturn(new StringEntity(content, Consts.UTF_8));

        return mockedResponse;
    }

    public static HttpClientFactoryWithTruststore mockHttpClientWithTruststore(int responseStatus, String responseContent) {

       /* CloseableHttpResponse response = mockHttpResponse(responseStatus, responseContent);
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        try {
            when(client.execute(ArgumentMatchers.<Class<HttpUriRequest>>any(), ArgumentMatchers.<Class<ResponseHandler<T>>>any())).thenReturn()
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpClientFactoryWithTruststore factory = mock(HttpClientFactoryWithTruststore.class);
        when(factory.createClient()).thenReturn(client);
        return factory;*/
        return null;
    }

    public static <T> Http mockHttpRequestWithResponse(T response) {
        Http.RequestExecutor executor = mock(Http.RequestExecutor.class);
        when(executor.executeWithJsonResponse(any())).thenReturn(response);

        Http request = mock(Http.class);
        when(request.post(any())).thenReturn(executor);
        return request;
    }

    /*public static HttpResponse mockHttpResponse(int statusCode, String content) {
        HttpResponse response = mock(HttpResponse.class);

        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusLine()).thenReturn(statusLine);

        HttpEntity entity = new StringEntity(content, Consts.UTF_8);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }*/

    public static HttpClientFactory mockClientFactory() {

        CloseableHttpClient client = mock(CloseableHttpClient.class);

        return () -> client;
    }


}
