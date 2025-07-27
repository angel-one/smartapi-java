package com.angelbroking.smartapi.orderupdate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
// import lombok.extern.slf4j.Slf4j; // Removed: No longer needed if manually declaring logger

// @Slf4j // Removed: Replaced with manual logger declaration
public class OrderUpdateServiceImpl implements OrderUpdateListner {
    private static final Logger log = LoggerFactory.getLogger(OrderUpdateServiceImpl.class);
    private SmartConnect smartConnect;
    private OrderUpdateWebsocket orderUpdateWebsocket;

    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onError(SmartStreamError error) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPong() {

    }

    @Override
    public void onOrderUpdate(String data) {
        log.info("order data {} ", data);
    }
}
