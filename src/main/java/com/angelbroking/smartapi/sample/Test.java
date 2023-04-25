package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.SmartStreamListenerImpl;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.angelbroking.smartapi.utils.ApiResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Test {

    public static void main(String[] args) throws SmartAPIException {
        try {

            String apiKey = "";
            String clientId = "";
            String clientPin = "";
            String tOTP = "";
            SmartConnect smartConnect = new SmartConnect(apiKey);

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

            // Generate User Session smartConnectParams
            User user = smartConnect.generateSession(clientId, clientPin, tOTP);
            smartConnect.setAccessToken(user.getAccessToken());
            smartConnect.setUserId(user.getUserId());

            // SmartStreamTicker
            String feedToken = user.getFeedToken();
            SmartStreamTicker ticker = new SmartStreamTicker(clientId, feedToken, new SmartStreamListenerImpl());
            ticker.connect();
            ticker.subscribe(SmartStreamSubsMode.QUOTE, getTokens());

            log.info("DONE");

            Examples examples = new Examples();
            log.info("getProfile");
            examples.getProfile(smartConnect);

            log.info("placeOrder");
           examples.placeOrder(smartConnect);

            log.info("modifyOrder");
            examples.modifyOrder(smartConnect);

            log.info("cancelOrder");
            examples.cancelOrder(smartConnect);

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
            ApiResponse createRuleID = examples.createRule(smartConnect);
            Gson gson = new Gson();
            String json = gson.toJson(createRuleID);
            JSONObject jsonObject = new JSONObject(json);
            JSONObject obj = jsonObject.getJSONObject("data");
            log.info("ModifyRule");
            ApiResponse apiResponse = examples.modifyRule(smartConnect, String.valueOf(jsonObject.getJSONObject("data").getInt("id")));
			log.info("cancelRule");
			examples.cancelRule(smartConnect,String.valueOf(jsonObject.getJSONObject("data").getInt("id")));

            log.info("Rule Details");
            examples.ruleDetails(smartConnect, String.valueOf(jsonObject.getJSONObject("data").getInt("id")));

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
