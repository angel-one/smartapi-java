package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;

public class Test {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

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
             * logger.info("session expired");
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

            logger.info("DONE");
            // token re-generate
//			TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
//					user.getRefreshToken());
//			smartConnect.setAccessToken(tokenSet.getAccessToken());

            Examples examples = new Examples();
            logger.info("getProfile");
            examples.getProfile(smartConnect);

            logger.info("placeOrder");
            Order placeOrder = examples.placeOrder(smartConnect);

//            logger.info("modifyOrder");
//           examples.modifyOrder(smartConnect, placeOrder);

            logger.info("cancelOrder");
            examples.cancelOrder(smartConnect, placeOrder);

            logger.info("getOrder");
            examples.getOrder(smartConnect);


            logger.info("getLTP");
            examples.getLTP(smartConnect);

            logger.info("getTrades");
            examples.getTrades(smartConnect);

            logger.info("getRMS");
            examples.getRMS(smartConnect);

            logger.info("getHolding");
            examples.getHolding(smartConnect);

            logger.info("getPosition");
            examples.getPosition(smartConnect);

            logger.info("convertPosition");
            examples.convertPosition(smartConnect);

            logger.info("createRule");
            String createRuleID = examples.createRule(smartConnect);

            logger.info("ModifyRule");
            String modifyRuleID = examples.modifyRule(smartConnect, createRuleID);

			logger.info("cancelRule");
			examples.cancelRule(smartConnect,modifyRuleID);

            logger.info("Rule Details");
            examples.ruleDetails(smartConnect, modifyRuleID);

            logger.info("Rule List");
            examples.ruleList(smartConnect);

            logger.info("Historic candle Data");
            examples.getCandleData(smartConnect);

            logger.info("logout");
            examples.logout(smartConnect);

        } catch (Exception e) {
            logger.info("Exception: " + e.getMessage());
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
