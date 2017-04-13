package requestHandling;


import constants.Params;

public class Answer {

    private int code;
    private String body;
    private byte[] content;

    public Answer(int code) {
        this(code, "");
    }

    Answer(int code, String body) {
        this.code = code;
        this.body = body;
    }

    Answer(int code, byte[] content) {
        this.code = code;
        this.content = content;
    }

    static Answer ok(String body) {
        return new Answer(Params.STATUS_OK, body);
    }

    static Answer ok(byte[] content) {
        return new Answer(Params.STATUS_OK, content);
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

    byte[] getContent() {
        return content;
    }
}
