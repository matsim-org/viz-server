package requestHandling;


import constants.Params;

public class Answer {

    private int code;
    private String body;

    public Answer(int code) {
        this(code, "");
    }

    Answer(int code, String body) {
        this.code = code;
        this.body = body;
    }

    static Answer ok(String body) {
        return new Answer(Params.STATUS_OK, body);
    }

    int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
