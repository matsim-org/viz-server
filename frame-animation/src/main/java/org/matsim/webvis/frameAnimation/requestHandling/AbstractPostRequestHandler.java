package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractPostRequestHandler<T> implements Route {

    private Class<T> classInfo;
    private final SimulationDataDAO simulationDataDAO = new SimulationDataDAO();

    AbstractPostRequestHandler(Class<T> classInfo) {

        this.classInfo = classInfo;
    }

    public abstract Answer process(T body);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        Answer answer = processBody(request.body());

        //send an error message as response
        if (!answer.isOk()) {
            response.body(answer.getText());
            response.status(answer.getCode());
            response.type(Params.RESPONSETYPE_TEXT);
            return answer.getText();
        }

        //send a response as JSON
        if (answer.hastText()) {
            response.body(answer.getText());
            response.status(answer.getCode());
            response.type(Params.RESPONSETYPE_JSON);
            response.header("Content-Encoding", "gzip");
            return answer.getText();
        }

        //send raw bytes as response
        String origin = request.headers("Origin");
        response.header("Access-Control-Allow-Origin", (origin != null) ? origin : "*");
        response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.header("Access-Control-Allow-Credentials", "true");
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
        response.type(Params.RESPONSETYPE_OCTET_STREAM);
        response.raw().setContentType(Params.RESPONSETYPE_OCTET_STREAM);
        response.raw().getOutputStream().write(answer.getEncodedMessage());
        response.raw().getOutputStream().close();
        return answer.getCode();
    }

    Answer processBody(String body) {
        T contractClass;
        try {
            contractClass = new Gson().fromJson(body, classInfo);
        } catch (JsonSyntaxException e) {
            return new Answer(Params.STATUS_BADREQUEST, "the request body did NOT have the correct format");
        }
        return process(contractClass);
    }

    protected SimulationDataDAO getData() {
        return simulationDataDAO;
    }
}
