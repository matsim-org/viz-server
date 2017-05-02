package requestHandling;


import constants.Params;

public class Answer {

    private int code;
    private byte[] encodedMessage;
    private String text;

    public Answer(int code, String text) {
        this.code = code;
        this.text = text;
    }

    Answer(int code, byte[] delimitedMessage) {
        this.code = code;
        encodedMessage = delimitedMessage;
    }

    static Answer ok(byte[] delimitedMessage) {
        return new Answer(Params.STATUS_OK, delimitedMessage);
    }

    static Answer ok(String message) {
        return new Answer(Params.STATUS_OK, message);
    }

    int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getEncodedMessage() {
        if (!hasEncodedMessage()) {
            throw new RuntimeException("no encoded message!");
        }
        return encodedMessage;
    }

    public String getText() {
        if (!hastText()) {
            throw new RuntimeException("no body set");
        }
        return text;
    }

    public boolean hasEncodedMessage() {
        return (this.encodedMessage != null);
    }

    public boolean hastText() {
        return (this.text != null);
    }

    public boolean isOk() {
        return (this.code == Params.STATUS_OK);
    }
}
