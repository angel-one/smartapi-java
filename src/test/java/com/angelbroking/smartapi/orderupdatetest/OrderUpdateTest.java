package com.angelbroking.smartapi.orderupdatetest;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.orderupdate.OrderUpdateListner;
import com.angelbroking.smartapi.orderupdate.OrderUpdateWebsocket;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.neovisionaries.ws.client.WebSocketException;
// Removed unused and unresolved import statement
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderUpdateTest {
    private static final Logger log = LoggerFactory.getLogger(SmartConnect.class);

    private static String clientID;
    private static String clientPass;
    private static String apiKey;
    private static String accessToken;
    private static String totp;

    @BeforeAll
    public void init(){
        clientID = System.getProperty("clientID");
        clientPass = System.getProperty("clientPass");
        apiKey = System.getProperty("apiKey");

        Scanner sc = new Scanner(System.in);
        log.info("enter totp: ");
        totp = sc.nextLine();

        SmartConnect smartConnect = new SmartConnect(apiKey);
        User user = smartConnect.generateSession(clientID, clientPass, totp);
        smartConnect.setAccessToken(user.getAccessToken());
        smartConnect.setUserId(user.getUserId());
        accessToken = user.getAccessToken();
    }

    @Test
    public void testOrderUpdate_success() throws WebSocketException {
        OrderUpdateWebsocket orderUpdateWebsocket = new OrderUpdateWebsocket(accessToken, new OrderUpdateListner() {
            @Override
            public void onConnected() {
                log.info("Connected");
            }

            @Override
            public void onDisconnected() {
                log.info("Disconnected");
            }

            @Override
            public void onError(SmartStreamError error) {
                log.info("error {} ",error.getException().getMessage());
            }

            @Override
            public void onPong() {
                log.info("Pong");
            }

            @Override
            public void onOrderUpdate(String data) {
                log.info("data {} ",data);
            }
        });

        orderUpdateWebsocket.connect();

    }
}
