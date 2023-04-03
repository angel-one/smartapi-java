package com.angelbroking.smartapi.utils;



/**
 * Contains all the Strings that are being used in the Smart API Connect library.
 */
public class Constants {

    private Constants() {

    }

    /** Product types. */
    public static final String PRODUCT_DELIVERY = "DELIVERY";
    public static final String PRODUCT_INTRADAY = "INTRADAY";
    public static final String PRODUCT_MARGIN = "MARGIN";
    public static final String PRODUCT_BO = "BO";
    public static final String PRODUCT_CARRYFORWARD = "CARRYFORWARD";

    /** Order types. */
    public static final String ORDER_TYPE_MARKET = "MARKET";
    public static final String ORDER_TYPE_LIMIT = "LIMIT";
    public static final String ORDER_TYPE_STOPLOSS_LIMIT = "STOPLOSS_LIMIT";
    public static final String ORDER_TYPE_STOPLOSS_MARKET = "STOPLOSS_MARKET";

    /** Variety types. */
    public static final String VARIETY_NORMAL = "NORMAL";
    public static final String VARIETY_AMO = "AMO";
    public static final String VARIETY_STOPLOSS = "STOPLOSS";
    public static final String VARIETY_ROBO = "ROBO";

    /** Transaction types. */
    public static final String TRANSACTION_TYPE_BUY = "BUY";
    public static final String TRANSACTION_TYPE_SELL = "SELL";

    /** Duration types. */
    public static final String DURATION_DAY = "DAY";
    public static final String DURATION_IOC = "IOC";

    /** Exchanges. */
    public static final String EXCHANGE_NSE = "NSE";
    public static final String EXCHANGE_BSE = "BSE";
    public static final String EXCHANGE_NFO = "NFO";
    public static final String EXCHANGE_CDS = "CDS";
    public static final String EXCHANGE_NCDEX = "NCDEX";
    public static final String EXCHANGE_MCX = "MCX";


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







}
