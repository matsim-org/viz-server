package org.matsim.webvis.common.util;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.service.UnauthorizedException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
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

    private static CloseableHttpResponse mockHttpResponse(int status, String content) {
        StatusLine mockedStatusLine = mock(StatusLine.class);
        when(mockedStatusLine.getStatusCode()).thenReturn(status);

        CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        try {
            when(mockedResponse.getEntity()).thenReturn(new StringEntity(content));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return mockedResponse;
    }

    public static HttpClientFactory mockHttpClientFactory(int responseStatus, String responseContent) {

        CloseableHttpResponse response = mockHttpResponse(responseStatus, responseContent);
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        try {
            when(client.execute(any())).thenReturn(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpClientFactory factory = mock(HttpClientFactory.class);
        when(factory.createClient()).thenReturn(client);
        return factory;
    }


}
