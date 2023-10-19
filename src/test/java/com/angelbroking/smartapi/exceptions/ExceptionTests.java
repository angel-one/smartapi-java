package com.angelbroking.smartapi.exceptions;

import com.angelbroking.smartapi.http.exceptions.*;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class ExceptionTests {
    @Test
    public void testDataException() {
        String message = "Test Message";
        String code = "Test Code";
        DataException exception = new DataException(message, code);
        assertEquals(message, exception.message);
        assertEquals(code, exception.code);
    }

    @Test
    public void testGeneralException() {
        String message = "Test Message";
        String code = "123";
        GeneralException exception = new GeneralException(message, code);

        assertEquals(message, exception.message);
        assertEquals(code, exception.code);
    }

    @Test
    public void testInputException() {
        InputException exception = new InputException("Test message", "Test code");
        assertEquals("Test message", exception.message);
        assertEquals("Test code", exception.code);
    }

    @Test
    public void testNetworkException() {
        NetworkException exception = new NetworkException("Test message", "Test code");
        assertEquals("Test message", exception.message);
        assertEquals("Test code", exception.code);
    }

    @Test
    public void testOrderException() {
        OrderException exception = new OrderException("Test message", "Test code");
        assertEquals("Test message", exception.message);
        assertEquals("Test code", exception.code);
    }

    @Test
    public void testPermissionException() {
        PermissionException exception = new PermissionException("Test message", "Test code");
        assertEquals("Test message", exception.message);
        assertEquals("Test code", exception.code);
    }

    @Test
    public void testTokenException() {
        String message = "Test message";
        String code = "123";
        TokenException tokenException = new TokenException(message, code);
        assertEquals(message, tokenException.message);
        assertEquals(code, tokenException.code);
    }

    @Test
    public void testApiKeyException() {
        String message = "Invalid API Key";
        String code = "123";
        ApiKeyException exception = new ApiKeyException(message, code);
        assertEquals(message, exception.message);
        assertEquals(code, exception.code);
    }

    @Test
    public void testSmartStreamError() {
        Throwable exception = new Throwable("Test Exception");
        SmartStreamError smartStreamError = new SmartStreamError();
        smartStreamError.setException(exception);
        assertEquals(exception, smartStreamError.getException());
    }
}
