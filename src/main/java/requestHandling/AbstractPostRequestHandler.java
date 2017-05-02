package requestHandling;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import constants.Params;
import data.MatsimDataProvider;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractPostRequestHandler<T> implements Route {

    protected MatsimDataProvider dataProvider;
    private Class<T> classInfo;

    protected AbstractPostRequestHandler(Class<T> classInfo, MatsimDataProvider dataProvider) {

        this.classInfo = classInfo;
        this.dataProvider = dataProvider;
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
        response.type(Params.RESPONSETYPE_OCTET_STREAM);
        response.raw().setContentType(Params.RESPONSETYPE_OCTET_STREAM);
        response.raw().getOutputStream().write(answer.getEncodedMessage());
        response.raw().getOutputStream().close();
        return answer.getCode();
    }

    protected Answer processBody(String body) {
        T contractClass;
        try {
            contractClass = new Gson().fromJson(body, classInfo);
        } catch (JsonSyntaxException e) {
            return new Answer(Params.STATUS_BADREQUEST, "the request body did NOT have the correct format");
        }
        return process(contractClass);
    }
}
