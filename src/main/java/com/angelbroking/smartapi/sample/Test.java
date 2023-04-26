package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.response.HttpResponse;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.SmartStreamListenerImpl;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

import static com.angelbroking.smartapi.utils.Constants.TIME_OUT_IN_MILLIS;

@Slf4j
public class Test {

    public static void main(String[] args) throws SmartAPIException {
        try {
            String apiKey = "zkWvUuLx";
            String clientId = "D541276";
            String clientPin = "1501";
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            String totp_key = "L6FMTTCWRVSK2PW6AF7A2YMO6Q";
            String tOTP = String.valueOf(gAuth.getTotpPassword(totp_key));
            Proxy proxy = Proxy.NO_PROXY;
            SmartConnect smartConnect = new SmartConnect(apiKey,proxy,TIME_OUT_IN_MILLIS);

            // Generate User Session smartConnectParams
            log.info("Generate session");
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
            HttpResponse placeOrder = examples.placeOrder(smartConnect);
            JSONObject placeOrderJSONObject = new JSONObject(placeOrder.getBody().toString());
            JSONObject placeOrderData = placeOrderJSONObject.getJSONObject("data");

            log.info("modifyOrder");
            examples.modifyOrder(smartConnect,String.valueOf(placeOrderData.getString ("orderid")));

            log.info("cancelOrder");
            examples.cancelOrder(smartConnect,String.valueOf(placeOrderData.getString ("orderid")));

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
            HttpResponse createRuleID = examples.createRule(smartConnect);
            JSONObject loginResultObject = new JSONObject(createRuleID.getBody().toString());
            JSONObject obj = loginResultObject.getJSONObject("data");
            log.info("ModifyRule");
            HttpResponse apiResponse = examples.modifyRule(smartConnect, String.valueOf(obj.getInt("id")));

			log.info("cancelRule");
			examples.cancelRule(smartConnect,String.valueOf(obj.getInt("id")));

            log.info("Rule Details");
            examples.ruleDetails(smartConnect, String.valueOf(obj.getInt("id")));

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
