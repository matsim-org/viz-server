package org.matsim.webvis.frameAnimation.requestHandling;

import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;

import static org.junit.Assert.*;

public class AnswerTest {

    @Test
    public void ok_fromEncodedMessage() {

        //arrange
        byte[] encodedMessage = new byte[]{1, 1, 1};

        //act
        Answer answer = Answer.ok(encodedMessage);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hasEncodedMessage());
        assertEquals(encodedMessage, answer.getEncodedMessage());
    }

    @Test
    public void ok_fromStringMessage() {

        //arrange
        String message = "some message";

        //act
        Answer answer = Answer.ok(message);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hastText());
        assertEquals(message, answer.getText());
    }

    @Test
    public void isOk() {

        //arrange
        Answer answer = new Answer(Params.STATUS_OK, "");

        //act
        boolean result = answer.isOk();

        //assert
        assertTrue(result);
    }

    @Test
    public void isOk_notOk() {
        //arrange
        Answer answer = new Answer(-1, "");

        //act
        boolean result = answer.isOk();

        //assert
        assertTrue(!result);
    }

    @Test
    public void hasEncodedMessage() {

        //arrange
        Answer answer = Answer.ok(new byte[1]);

        //act
        boolean result = answer.hasEncodedMessage();

        //assert
        assertTrue(result);
    }

    @Test
    public void hasEncodedMessage_false() {

        //arrange
        Answer answer = Answer.ok("");

        //act
        boolean result = answer.hasEncodedMessage();

        //assert
        assertTrue(!result);
    }

    @Test
    public void hasText() {

        //arrange
        Answer answer = Answer.ok("");

        //act
        boolean result = answer.hastText();

        //assert
        assertTrue(result);
    }

    @Test
    public void hasText_false() {

        //arrange
        Answer answer = Answer.ok(new byte[1]);

        //act
        boolean result = answer.hastText();

        //assert
        assertTrue(!result);
    }

    @Test(expected = RuntimeException.class)
    public void getEncodedMessage_noMessage() {

        //arrange
        Answer answer = Answer.ok("");

        //act
        answer.getEncodedMessage();

        //assert
        fail();
    }

    @Test(expected = RuntimeException.class)
    public void getText_noMessage() {

        //arrange
        Answer answer = Answer.ok(new byte[1]);

        //act
        answer.getText();

        //assert
        fail();
    }
}
