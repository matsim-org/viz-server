package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InternalException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import java.io.IOException;

public abstract class HttpResponseHandler<T> implements ResponseHandler<T> {

    private static Gson gson = new Gson();

    protected abstract T processResponse(HttpResponse httpResponse);

    @Override
    public T handleResponse(HttpResponse httpResponse) {
        if (isNotOK(httpResponse)) generateError(httpResponse);
        return processResponse(httpResponse);
    }

    private boolean isNotOK(HttpResponse response) {
        return response.getStatusLine().getStatusCode() != org.apache.http.HttpStatus.SC_OK;
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
