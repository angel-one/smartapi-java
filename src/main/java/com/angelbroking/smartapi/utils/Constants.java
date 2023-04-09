package com.angelbroking.smartapi.utils;


import org.json.JSONObject;

/**
 * Contains all the Strings that are being used in the Smart API Connect library.
 */
public class Constants {

    private Constants() {

    }

    /** Product types. */
    public static final String PRODUCT_DELIVERY = "DELIVERY";
    public static final String PRODUCT_INTRADAY = "INTRADAY";


    /** Order types. */
    public static final String ORDER_TYPE_LIMIT = "LIMIT";
    public static final String ORDER_TYPE_STOPLOSS_LIMIT = "STOPLOSS_LIMIT";

    /** Variety types. */
    public static final String VARIETY_NORMAL = "NORMAL";
    public static final String VARIETY_STOPLOSS = "STOPLOSS";

    /** Transaction types. */
    public static final String TRANSACTION_TYPE_BUY = "BUY";

    /** Duration types. */
    public static final String DURATION_DAY = "DAY";

    /** Exchanges. */
    public static final String EXCHANGE_NSE = "NSE";


    /** SmartStream Routes */
    public static final String ROOT_URL  = "https://apiconnect.angelbroking.com";
    public static final String LOGIN_URL  = "https://apiconnect.angelbroking.com/rest/auth/angelbroking/user/v1/loginByPassword";
    public static final String WSURI  = "wss://wsfeeds.angelbroking.com/NestHtml5Mobile/socket/stream";
    public static final String SMARTSTREAM_WSURI  = "ws://smartapisocket.angelone.in/smart-stream";
    public static final String SWSURI  = "wss://smartapisocket.angelbroking.com/websocket";

    /** User Model Constants */
    public static final String USER_DATA  = "data";
    public static final String USER_PRODUCTS  = "products";
    public static final String USER_EXCHANGES  = "exchanges";


    /** Smart WebSocket Constants */
    public static final String ACTION_TYPE  = "actiontype";
    public static final String FEEED_TYPE  = "feedtype";
    public static final String JWT_TOKEN  = "jwttoken";
    public static final String CLIENT_CODE  = "clientcode";
    public static final String API_KEY  = "apikey";

    /** SmartAPITicker Constants */
    public static final String SmartAPITicker_task  = "task";
    public static final String SmartAPITicker_channel  = "channel";
    public static final String SmartAPITicker_token  = "token";
    public static final String SmartAPITicker_user  = "user";
    public static final String SmartAPITicker_acctid  = "acctid";



    /** SmartConnect Constants */
    public static final String SmartConnect_clientcode  = "clientcode";
    public static final String SmartConnect_password  = "password";
    public static final String SmartConnect_totp  = "totp";
    public static final String SmartConnect_data  = "data";
    public static final String SmartConnect_api_refresh  = "api.refresh";
    public static final String SmartConnect_jwtToken  = "jwtToken";
    public static final String SmartConnect_refreshToken  = "refreshToken";
    public static final String SmartConnect_feedToken  = "feedToken";
    public static final String SmartConnect_api_user_profile  = "api.user.profile";
    public static final String SmartConnect_checksum  = "checksum";

    /** SmartConnect Routes Constants */
    public static final String SmartConnect_api_order_place  = "api.order.place";
    public static final String SmartConnect_api_order_modify  = "api.order.modify";
    public static final String SmartConnect_api_order_cancel  = "api.order.cancel";
    public static final String SmartConnect_api_order_book  = "api.order.book";
    public static final String SmartConnect_api_ltp_data  = "api.ltp.data";
    public static final String SmartConnect_api_order_trade_book  = "api.order.trade.book";
    public static final String SmartConnect_api_order_rms_data  = "api.order.rms.data";
    public static final String SmartConnect_api_order_rms_holding  = "api.order.rms.holding";
    public static final String SmartConnect_api_order_rms_position  = "api.order.rms.position";
    public static final String SmartConnect_api_order_rms_position_convert  = "api.order.rms.position.convert";
    public static final String SmartConnect_api_gtt_create  = "api.gtt.create";
    public static final String SmartConnect_api_gtt_modify  = "api.gtt.modify";
    public static final String SmartConnect_api_gtt_cancel  = "api.gtt.cancel";
    public static final String SmartConnect_api_gtt_details  = "api.gtt.details";
    public static final String SmartConnect_api_gtt_list  = "api.gtt.list";
    public static final String SmartConnect_api_candle_data  = "api.candle.data";
    public static final String SmartConnect_api_user_logout  = "api.user.logout";
//    public static final String SmartConnect_  = "";
//    public static final String SmartConnect_  = "";
//    public static final String SmartConnect_  = "";
//    public static final String SmartConnect_  = "";











}
