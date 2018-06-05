package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.matsim.webvis.common.service.InternalException;
import org.matsim.webvis.common.util.TestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HttpTest {

    @Test
    public void executor_withCredential_authorizationHeaderSet() {

        HttpPost post = new HttpPost(URI.create("http://some.uri"));
        final String headerContent = "header content";
        Http.RequestExecutor testObject = new Http.RequestExecutor(post, () -> null);

        testObject.withCredential(() -> headerContent);
        assertEquals(headerContent, post.getFirstHeader(Http.AUTHORIZATION).getValue());
    }

    @Test(expected = InternalException.class)
    public void executor_withEntityBody_requestNotEnclosing_internalException() {

        HttpGet get = new HttpGet("http://some.uri");
        Http.RequestExecutor testObject = new Http.RequestExecutor(get, () -> null);

        testObject.withEntityBody(new StringEntity("content", Consts.UTF_8));
    }

    @Test
    public void executor_withEntityBody_entityIsSet() throws IOException {

        HttpPost post = new HttpPost("http://some.uri");
        HttpEntity entity = new StringEntity("content", Consts.UTF_8);
        Http.RequestExecutor testObject = new Http.RequestExecutor(post, () -> null);

        testObject.withEntityBody(entity);

        assertEquals(EntityUtils.toString(entity), EntityUtils.toString(post.getEntity()));
    }

    @Test
    public void executor_withJsonBody_EntityAndContentTypeSet() throws IOException {

        HttpPost post = new HttpPost("http://some.uri");
        ErrorResponse jsonBody = new ErrorResponse("code", "description"); //this could be any other type
        Http.RequestExecutor testObject = new Http.RequestExecutor(post, () -> null);

        testObject.withJsonBody(jsonBody);

        assertEquals(new Gson().toJson(jsonBody), EntityUtils.toString(post.getEntity()));
    }

    @Test
    public void executor_executeWithTextResponse_textResponse() throws IOException {

        final String response = "hello";
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        doReturn(response).when(client).execute(any(), any(HttpResponseHandler.class));
        Http.RequestExecutor testObject = new Http.RequestExecutor(new HttpPost("http://some.uri"), () -> client);

        String received = testObject.executeWithTextResponse();

        assertEquals("hello", received);
    }

    @Test
    public void executor_executeWithJsonResponse_responseObject() throws IOException {

        ErrorResponse response = new ErrorResponse("code", "description");
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        doReturn(response).when(client).execute(any(), any(HttpResponseHandler.class));
        Http.RequestExecutor testObject = new Http.RequestExecutor(new HttpPost("http://some.uri"), () -> client);

        ErrorResponse received = testObject.executeWithJsonResponse(ErrorResponse.class);

        assertEquals(response, received);
    }

    @Test
    public void executor_executeWithStreamResponse_inputStream() throws IOException {

        InputStream stream = new InputStream() {
            @Override
            public int read() {
                return 0;
            }
        };
        CloseableHttpClient client = mock(CloseableHttpClient.class);
        doReturn(stream).when(client).execute(any(), any(HttpResponseHandler.class));
        Http.RequestExecutor testObject = new Http.RequestExecutor(new HttpPost("http://some.uri"), () -> client);

        InputStream received = testObject.executeWithStreamResponse();

        assertEquals(stream, received);
    }

    @Test(expected = InternalException.class)
    public void executor_execute_clientThrowsIoException_internalException() throws IOException {

        CloseableHttpClient client = mock(CloseableHttpClient.class);
        when(client.execute(any(), any(HttpResponseHandler.class))).thenThrow(new IOException("error"));
        Http.RequestExecutor testObject = new Http.RequestExecutor(new HttpPost("http://some.uri"), () -> client);

        testObject.executeWithTextResponse(); // execute itself is private
    }

    @Test
    public void executor_jsonResponseHandler_responseObject() {

        ErrorResponse jsonResponse = new ErrorResponse("code", "description"); //this could be any type
        Http.RequestExecutor executor = new Http.RequestExecutor(null, () -> null);
        HttpResponse response = TestUtils.mockHttpResponse(HttpStatus.OK, new Gson().toJson(jsonResponse));
        HttpResponseHandler<ErrorResponse> testObject = executor.createJsonResponseHandler(ErrorResponse.class);

        ErrorResponse receivedResponse = testObject.handleResponse(response);

        assertEquals(jsonResponse.getError_description(), receivedResponse.getError_description());
        assertEquals(jsonResponse.getError(), receivedResponse.getError());
    }

    @Test(expected = InternalException.class)
    public void executor_jsonResponseHandlerNoJson_responseObject() {

        Http.RequestExecutor executor = new Http.RequestExecutor(null, () -> null);
        HttpResponse response = TestUtils.mockHttpResponse(HttpStatus.OK, "not json");
        HttpResponseHandler<ErrorResponse> testObject = executor.createJsonResponseHandler(ErrorResponse.class);

        testObject.handleResponse(response);
    }

    @Test
    public void executor_textResponseHandler_stringResponse() {

        final String responseContent = "some content";
        Http.RequestExecutor executor = new Http.RequestExecutor(null, () -> null);
        HttpResponse response = TestUtils.mockHttpResponse(HttpStatus.OK, responseContent);
        HttpResponseHandler<String> testObject = executor.createTextResponseHandler();

        String processed = testObject.handleResponse(response);

        assertEquals(responseContent, processed);
    }

    @Test
    public void executor_streamResponseHandler_inputStreamResponse() throws IOException {

        final String responseContent = "some content";
        Http.RequestExecutor executor = new Http.RequestExecutor(null, () -> null);
        HttpResponse response = TestUtils.mockHttpResponse(HttpStatus.OK, responseContent);
        HttpResponseHandler<InputStream> testObject = executor.createInputStreamHandler();

        InputStream stream = testObject.handleResponse(response);

        String streamedResponse = new BufferedReader((new InputStreamReader(stream))).readLine();
        assertEquals(responseContent, streamedResponse);
    }
}
