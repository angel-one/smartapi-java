package com.angelbroking.smartapi.http;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SmartAPIRequestHandlerTest {
    private SmartAPIRequestHandler requestHandler;

    @Mock
    private Proxy mockProxy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestHandler = new SmartAPIRequestHandler(mockProxy);
    }

    @Test
    public void testApiHeaders() throws IOException {
        // Set up mock for URLStreamHandlerFactory
        URLStreamHandlerFactory mockURLStreamHandlerFactory = Mockito.mock(URLStreamHandlerFactory.class);
        URL.setURLStreamHandlerFactory(mockURLStreamHandlerFactory);

        // Set up mock URLConnection and BufferedReader
        URLConnection mockURLConnection = Mockito.mock(URLConnection.class);
        Mockito.when(mockURLConnection.getInputStream()).thenReturn(
                new ByteArrayInputStream("123.456.789.123".getBytes(StandardCharsets.UTF_8)));
        BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(mockBufferedReader.readLine()).thenReturn("123.456.789.123");
        Mockito.when(mockURLConnection.getInputStream()).thenReturn(
                new ByteArrayInputStream("123.456.789.123".getBytes(StandardCharsets.UTF_8)));
        Mockito.when(mockURLConnection.getInputStream()).thenReturn(
                new ByteArrayInputStream("123.456.789.123".getBytes(StandardCharsets.UTF_8)));
        URLStreamHandler mockURLStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                return mockURLConnection;
            }
        };
        Mockito.when(mockURLStreamHandlerFactory.createURLStreamHandler(Mockito.anyString())).thenReturn(
                mockURLStreamHandler);

        // Call apiHeaders and assert expected results
        JSONObject headers = requestHandler.apiHeaders();
        assertNotNull(headers);
        assertEquals("123.456.789.123", headers.getString("clientPublicIP"));
        assertNotNull(headers.getString("clientLocalIP"));
        assertNotNull(headers.getString("macAddress"));
        assertEquals("application/json", headers.getString("accept"));
        assertEquals("USER", headers.getString("userType"));
        assertEquals("WEB", headers.getString("sourceID"));
    }
}
