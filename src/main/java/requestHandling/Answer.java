package requestHandling;


import com.google.protobuf.MessageLite;
import constants.Params;

public class Answer {

    private int code;
    private MessageLite message;
    private byte[] encodedMessage;
    private String text;

    public Answer(int code, String text) {
        this.code = code;
        this.text = text;
    }

    Answer(int code, MessageLite message) {
        this.code = code;
        this.message = message;
    }

    Answer(int code, byte[] delimitedMessage) {
        this.code = code;
        encodedMessage = delimitedMessage;
    }

    static Answer ok(byte[] delimitedMessage) {
        return new Answer(Params.STATUS_OK, delimitedMessage);
    }

    static Answer ok(MessageLite message) {
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

    public MessageLite getMessage() {
        if (!hasMessage()) {
            throw new RuntimeException("no message set");
        }
        return message;
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

    public boolean hasMessage() {
        return (this.message != null);
    }

    public boolean hastText() {
        return (this.text != null);
    }
}
