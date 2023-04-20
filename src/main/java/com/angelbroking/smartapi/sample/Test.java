package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import lombok.extern.slf4j.Slf4j;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Test {

    public static void main(String[] args) throws SmartAPIException {
        try {

            String apiKey = "zkWvUuLx";
            String clientId = "D541276";
            String clientPin = "1501";
            String tOTP = "589528";
            SmartConnect smartConnect = new SmartConnect(apiKey);
            SmartAPIRequestHandler smartAPIRequestHandler = new SmartAPIRequestHandler(Proxy.NO_PROXY);


            // OPTIONAL - ACCESS_TOKEN AND REFRESH TOKEN
            /*
             * SmartConnect smartConnect = new martConnect("<api_key>", "<YOUR_ACCESS_TOKEN>", "<YOUR_REFRESH_TOKEN>");
             */

            // Set session expiry callback.
            /*
             * smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
             * @Override
             * public void sessionExpired() {
             * log.info("session expired");
             * }
             * });
             */

            // Generate User Session
            User user = smartConnect.generateSession(clientId, clientPin, tOTP);
            smartConnect.setAccessToken(user.getAccessToken());
            smartConnect.setUserId(user.getUserId());

            // SmartStreamTicker
            String feedToken = user.getFeedToken();
            SmartStreamTicker ticker = new SmartStreamTicker(clientId, feedToken, new SmartStreamListenerImplTest());
            ticker.connect();
            ticker.subscribe(SmartStreamSubsMode.QUOTE, getTokens());

            log.info("DONE");

            Examples examples = new Examples();
            log.info("getProfile");
            examples.getProfile(smartConnect);

            log.info("placeOrder");
            Order placeOrder = examples.placeOrder(smartConnect);

//            log.info("modifyOrder");
//           examples.modifyOrder(smartConnect, placeOrder);

            log.info("cancelOrder");
            examples.cancelOrder(smartConnect, placeOrder);

            log.info("getOrder");
            examples.getOrder(smartConnect);


            log.info("getLTP");
            examples.getLTP(smartConnect);

            log.info("getTrades");
            examples.getTrades(smartConnect);

            log.info("getRMS");
            examples.getRMS(smartConnect);

            log.info("getHolding");
            examples.getHolding(smartConnect);

            log.info("getPosition");
            examples.getPosition(smartConnect);

            log.info("convertPosition");
            examples.convertPosition(smartConnect);

            log.info("createRule");
            String createRuleID = examples.createRule(smartConnect);

            log.info("ModifyRule");
            String modifyRuleID = examples.modifyRule(smartConnect, createRuleID);

			log.info("cancelRule");
			examples.cancelRule(smartConnect,modifyRuleID);

            log.info("Rule Details");
            examples.ruleDetails(smartConnect, modifyRuleID);

            log.info("Rule List");
            examples.ruleList(smartConnect);

            log.info("Historic candle Data");
            examples.getCandleData(smartConnect);

            log.info("logout");
            examples.logout(smartConnect);

        } catch (Exception e) {
            log.info("Exception: {}", e.getMessage());
        }

    }

    private static Set<TokenID> getTokens() {
        // find out the required token from
        // https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json
        Set<TokenID> tokenSet = new HashSet<>();
        tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
        tokenSet.add(new TokenID(ExchangeType.NSE_CM, "1594")); // NSE Infosys
        tokenSet.add(new TokenID(ExchangeType.NCX_FO, "GUARGUM5")); // GUAREX (NCDEX)
        return tokenSet;
    }
}
