package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.apache.http.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.InternalException;
import org.matsim.webvis.common.service.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.function.Supplier;

public class Http {

    static final String AUTHORIZATION = "Authorization";

    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();

    private HttpClientFactory clientFactory;

    public Http(HttpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public Http() {
        this.clientFactory = HttpClients::createDefault;
    }

    public RequestExecutor post(URI uri) {
        return new RequestExecutor(new HttpPost(uri), () -> clientFactory.createClient());
    }

    public static class RequestExecutor {

        private HttpUriRequest request;
        private Supplier<CloseableHttpClient> client;

        RequestExecutor(HttpUriRequest request, Supplier<CloseableHttpClient> client) {
            this.request = request;
            this.client = client;
        }

        public RequestExecutor withCredential(HttpCredential credential) {
            request.setHeader(AUTHORIZATION, credential.headerValue());
            return this;
        }

        public RequestExecutor withJsonBody(Object content) {
            withEntityBody(new StringEntity(gson.toJson(content), Consts.UTF_8));
            request.setHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON);
            return this;
        }

        public RequestExecutor withEntityBody(HttpEntity entity) {
            if (request instanceof HttpEntityEnclosingRequest)
                ((HttpEntityEnclosingRequest) request).setEntity(entity);
            else
                throw new InternalException("Can not set body on request method: " + this.request.getMethod());
            return this;
        }

        public <T> T executeWithJsonResponse(Class<T> responseType) {

            return execute(createJsonResponseHandler(responseType));
        }

        public String executeWithTextResponse() {
            return execute(createTextResponseHandler());
        }

        public InputStream executeWithStreamResponse() {
            return execute(createInputStreamHandler());
        }

        private <T> T execute(ResponseHandler<T> handler) {
            try (CloseableHttpClient client = this.client.get()) {
                logger.info("making request to: " + request.getURI().toString());
                return client.execute(request, handler);
            } catch (IOException e) {
                throw new InternalException("Request to: " + request.getURI().toString() + " failed.");
            }
        }

        <T> HttpResponseHandler<T> createJsonResponseHandler(Class<T> responseType) {
            return new HttpResponseHandler<T>() {
                @Override
                protected T processResponse(HttpResponse httpResponse) {

                    try {
                        Reader reader = new InputStreamReader(httpResponse.getEntity().getContent());
                        return gson.fromJson(reader, responseType);
                    } catch (IOException | JsonSyntaxException | JsonIOException e) {
                        throw new InternalException("Could not parse json response to: " + responseType.toString());
                    }
                }
            };
        }

        HttpResponseHandler<String> createTextResponseHandler() {
            return new HttpResponseHandler<String>() {
                @Override
                protected String processResponse(HttpResponse httpResponse) {
                    try {
                        return EntityUtils.toString(httpResponse.getEntity());
                    } catch (IOException e) {
                        throw new InternalException("Could not parse response to text");
                    }
                }
            };
        }

        HttpResponseHandler<InputStream> createInputStreamHandler() {
            return new HttpResponseHandler<InputStream>() {
                @Override
                protected InputStream processResponse(HttpResponse httpResponse) {
                    try {
                        return httpResponse.getEntity().getContent();
                    } catch (IOException e) {
                        throw new InternalException("Could not read InputStream from http response");
                    }
                }
            };
        }

        private boolean isNotOK(HttpResponse response) {
            return response.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
        }

        private void generateError(HttpResponse response) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
                throw new UnauthorizedException("Server returned 401: Unauthorized");
            try {
                ErrorResponse error = gson.fromJson(EntityUtils.toString(response.getEntity()), ErrorResponse.class);
                throw new CodedException(error.getError(), error.getError_description());
            } catch (IOException | JsonSyntaxException e) {
                throw new InternalException("Http request unsuccessful. Status: " + response.getStatusLine().getStatusCode());
            }
        }
    }
}
