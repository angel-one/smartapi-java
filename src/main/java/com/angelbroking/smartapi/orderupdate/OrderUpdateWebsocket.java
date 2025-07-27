package com.angelbroking.smartapi.orderupdate;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.utils.Utils;
import com.neovisionaries.ws.client.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OrderUpdateWebsocket {
    private static final Logger log = LoggerFactory.getLogger(OrderUpdateWebsocket.class);
    private static final int pingIntervalInMilliSeconds = 10000; // 10 seconds
    private static final String headerAuthorization = "Authorization";
    private static final Integer delayInMilliSeconds = 5000;
    private static final Integer periodInMilliSeconds = 5000;
    private final Routes routes = new Routes();
    private final String wsuri = routes.getOrderUpdateUri();
    private WebSocket ws;
    private String accessToken;
    private final OrderUpdateListner orderUpdateListner;

    private Timer pingTimer;
    private LocalDateTime lastPongReceivedTime = LocalDateTime.now();

    /**
     * Initializes the OrderUpdateWebsocket.
     *
     * @param accessToken
     * @param orderUpdateListner
     */
    public OrderUpdateWebsocket(String accessToken, OrderUpdateListner orderUpdateListner) {
        if (StringUtils.isEmpty(accessToken) || Utils.validateInputNullCheck(orderUpdateListner)) {

            throw new IllegalArgumentException(
                    "clientId, feedToken and SmartStreamListener should not be empty or null");
        }
        this.accessToken = accessToken;
        this.orderUpdateListner = orderUpdateListner;

        init();
    }

    private void init() {
        try {
            log.info("inside websocket init");
            log.info("accessToken {} ", accessToken);
            log.info("wsuri = {}", wsuri);
            ws = new WebSocketFactory()
                    .setVerifyHostname(false)
                    .createSocket(wsuri)
                    .setPingInterval(pingIntervalInMilliSeconds);
            ws.addHeader(headerAuthorization, "Bearer " + accessToken);
            ws.addListener(getWebsocketAdapter());
        } catch (IOException e) {
            if (Utils.validateInputNotNullCheck(orderUpdateListner)) {
                orderUpdateListner.onError(getErrorHolder(e));
            }
        }
    }

    private WebSocketListener getWebsocketAdapter() {
        return new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
                orderUpdateListner.onConnected();
                startPingTimer(websocket);
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                try {
                    orderUpdateListner.onOrderUpdate(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * On disconnection, return statement ensures that the thread ends.
             *
             * @param websocket
             * @param serverCloseFrame
             * @param clientCloseFrame
             * @param closedByServer
             * @throws Exception
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer) {
                try {
                    if (closedByServer) {
                        reconnect();
                    } else {
                        stopPingTimer();
                        orderUpdateListner.onDisconnected();
                    }
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    orderUpdateListner.onError(error);
                }
            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                super.onCloseFrame(websocket, frame);
            }

            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                try {
                    lastPongReceivedTime = LocalDateTime.now();
                    orderUpdateListner.onPong();
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    orderUpdateListner.onError(error);
                }
            }
        };
    }

    private void startPingTimer(final WebSocket websocket) {

        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (lastPongReceivedTime.isBefore(currentTime.minusSeconds(20))) {
                        websocket.disconnect();
                        reconnect();
                    }
                } catch (Exception e) {
                    orderUpdateListner.onError(getErrorHolder(e));
                }
            }
        }, delayInMilliSeconds, periodInMilliSeconds); // run at every 5 second
    }

    private void stopPingTimer() {
        if (Utils.validateInputNotNullCheck(pingTimer)) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    private void reconnect() throws WebSocketException {
        log.info("reconnect - started");
        init();
        connect();
        log.info("reconnect - done");
    }

    private SmartStreamError getErrorHolder(Exception e) {
        SmartStreamError error = new SmartStreamError();
        error.setException(e);
        return error;
    }

    public void connect() throws WebSocketException {
        ws.connect();
        log.info("connected to uri: {}", wsuri);
    }
}
