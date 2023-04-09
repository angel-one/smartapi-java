package com.angelbroking.smartapi.routes;

import java.util.HashMap;
import java.util.Map;

public class ApiRoutes {

    public static final String secureCommonPath = "/rest/secure/angelbroking";
    public static final String authCommonPath = "/rest/auth/angelbroking/jwt/v1";
    public static final String gttServiceCommonPath = "/gtt-service/rest/secure/angelbroking/gtt/v1";

    public static final Map<String, String> ROUTES = new HashMap<String, String>() {{
        StringBuilder sb = new StringBuilder();
       put("api.token", sb.append(authCommonPath).append("/generateTokens").toString());
        sb.setLength(0);
       put("api.user.profile", sb.append(secureCommonPath).append("/user/v1/getProfile").toString());
        sb.setLength(0);
       put("api.refresh", sb.append(authCommonPath).append("/generateTokens").toString());
        sb.setLength(0);
       put("api.user.logout", sb.append(secureCommonPath).append("/user/v1/logout").toString());
        sb.setLength(0);
       put("api.order.place", sb.append(secureCommonPath).append("/order/v1/placeOrder").toString());
        sb.setLength(0);
       put("api.order.modify", sb.append(secureCommonPath).append("/order/v1/modifyOrder").toString());
        sb.setLength(0);
       put("api.order.cancel", sb.append(secureCommonPath).append("/order/v1/cancelOrder").toString());
        sb.setLength(0);
       put("api.order.book", sb.append(secureCommonPath).append("/order/v1/getOrderBook").toString());
        sb.setLength(0);
       put("api.order.trade.book", sb.append(secureCommonPath).append("/order/v1/getTradeBook").toString());
        sb.setLength(0);
       put("api.order.rms.data", sb.append(secureCommonPath).append("/user/v1/getRMS").toString());
        sb.setLength(0);
       put("api.order.rms.holding", sb.append(secureCommonPath).append("/portfolio/v1/getHolding").toString());
        sb.setLength(0);
       put("api.order.rms.position", sb.append(secureCommonPath).append("/order/v1/getPosition").toString());
        sb.setLength(0);
       put("api.order.rms.position.convert", sb.append(secureCommonPath).append("/order/v1/convertPosition").toString());
        sb.setLength(0);
       put("api.ltp.data", sb.append(secureCommonPath).append("/order/v1/getLtpData").toString());
        sb.setLength(0);
       put("api.gtt.create", sb.append(gttServiceCommonPath).append("/createRule").toString());
        sb.setLength(0);
       put("api.gtt.modify", sb.append(gttServiceCommonPath).append("/modifyRule").toString());
        sb.setLength(0);
       put("api.gtt.cancel", sb.append(gttServiceCommonPath).append("/cancelRule").toString());
        sb.setLength(0);
       put("api.gtt.details", sb.append(secureCommonPath).append("/gtt/v1/ruleDetails").toString());
        sb.setLength(0);
       put("api.gtt.list", sb.append(secureCommonPath).append("/gtt/v1/ruleList").toString());
        sb.setLength(0);
       put("api.candle.data", sb.append(secureCommonPath).append("/historical/v1/getCandleData").toString());
    }};
}
