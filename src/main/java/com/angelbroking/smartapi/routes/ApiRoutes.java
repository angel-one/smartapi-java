package com.angelbroking.smartapi.routes;

import java.util.HashMap;
import java.util.Map;

public class ApiRoutes {

    public static final String SECURE_COMMON_PATH = "/rest/secure/angelbroking";
    public static final String AUTH_COMMON_PATH = "/rest/auth/angelbroking/jwt/v1";
    public static final String GTT_SERVICE_COMMON_PATH = "/gtt-service/rest/secure/angelbroking/gtt/v1";
    public static final Map<String, String> ROUTES;

    static {
        ROUTES =  new HashMap<>();
        StringBuilder sb = new StringBuilder();
        ROUTES.put("api.token", sb.append(AUTH_COMMON_PATH).append("/generateTokens").toString());
        sb.setLength(0);
        ROUTES.put("api.user.profile", sb.append(SECURE_COMMON_PATH).append("/user/v1/getProfile").toString());
        sb.setLength(0);
        ROUTES.put("api.refresh", sb.append(AUTH_COMMON_PATH).append("/generateTokens").toString());
        sb.setLength(0);
        ROUTES.put("api.user.logout", sb.append(SECURE_COMMON_PATH).append("/user/v1/logout").toString());
        sb.setLength(0);
        ROUTES.put("api.order.place", sb.append(SECURE_COMMON_PATH).append("/order/v1/placeOrder").toString());
        sb.setLength(0);
        ROUTES.put("api.order.modify", sb.append(SECURE_COMMON_PATH).append("/order/v1/modifyOrder").toString());
        sb.setLength(0);
        ROUTES.put("api.order.cancel", sb.append(SECURE_COMMON_PATH).append("/order/v1/cancelOrder").toString());
        sb.setLength(0);
        ROUTES.put("api.order.book", sb.append(SECURE_COMMON_PATH).append("/order/v1/getOrderBook").toString());
        sb.setLength(0);
        ROUTES.put("api.order.trade.book", sb.append(SECURE_COMMON_PATH).append("/order/v1/getTradeBook").toString());
        sb.setLength(0);
        ROUTES.put("api.order.rms.data", sb.append(SECURE_COMMON_PATH).append("/user/v1/getRMS").toString());
        sb.setLength(0);
        ROUTES.put("api.order.rms.holding", sb.append(SECURE_COMMON_PATH).append("/portfolio/v1/getHolding").toString());
        sb.setLength(0);
        ROUTES.put("api.order.rms.position", sb.append(SECURE_COMMON_PATH).append("/order/v1/getPosition").toString());
        sb.setLength(0);
        ROUTES.put("api.order.rms.position.convert", sb.append(SECURE_COMMON_PATH).append("/order/v1/convertPosition").toString());
        sb.setLength(0);
        ROUTES.put("api.ltp.data", sb.append(SECURE_COMMON_PATH).append("/order/v1/getLtpData").toString());
        sb.setLength(0);
        ROUTES.put("api.gtt.create", sb.append(GTT_SERVICE_COMMON_PATH).append("/createRule").toString());
        sb.setLength(0);
        ROUTES.put("api.gtt.modify", sb.append(GTT_SERVICE_COMMON_PATH).append("/modifyRule").toString());
        sb.setLength(0);
        ROUTES.put("api.gtt.cancel", sb.append(GTT_SERVICE_COMMON_PATH).append("/cancelRule").toString());
        sb.setLength(0);
        ROUTES.put("api.gtt.details", sb.append(SECURE_COMMON_PATH).append("/gtt/v1/ruleDetails").toString());
        sb.setLength(0);
        ROUTES.put("api.gtt.list", sb.append(SECURE_COMMON_PATH).append("/gtt/v1/ruleList").toString());
        sb.setLength(0);
        ROUTES.put("api.candle.data", sb.append(SECURE_COMMON_PATH).append("/historical/v1/getCandleData").toString());
    }
}
