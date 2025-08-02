package com.angelbroking.smartapi.utils;

/**
 * Contains all the Strings that are being used in the Smart API Connect library.
 */
public class Constants {

    /** Product types. */
	public static String PRODUCT_DELIVERY = "DELIVERY";
	public static String PRODUCT_INTRADAY = "INTRADAY";
	public static String PRODUCT_MARGIN = "MARGIN";
	public static String PRODUCT_BO = "BO";
	public static String PRODUCT_CARRYFORWARD = "CARRYFORWARD";

    /** Order types. */
    public static String ORDER_TYPE_MARKET = "MARKET";
    public static String ORDER_TYPE_LIMIT = "LIMIT";
    public static String ORDER_TYPE_STOPLOSS_LIMIT = "STOPLOSS_LIMIT";
    public static String ORDER_TYPE_STOPLOSS_MARKET = "STOPLOSS_MARKET";

    /** Variety types. */
    public static String VARIETY_NORMAL = "NORMAL";
    public static String VARIETY_AMO = "AMO";
    public static String VARIETY_STOPLOSS = "STOPLOSS";
    public static String VARIETY_ROBO = "ROBO";
    
    /** Transaction types. */
    public static String TRANSACTION_TYPE_BUY = "BUY";
    public static String TRANSACTION_TYPE_SELL = "SELL";

    /** Duration types. */
    public static String DURATION_DAY = "DAY";
    public static String DURATION_IOC = "IOC";

    /** Exchanges. */
    public static String EXCHANGE_NSE = "NSE";
    public static String EXCHANGE_BSE = "BSE";
    public static String EXCHANGE_NFO = "NFO";
    public static String EXCHANGE_CDS = "CDS";
    public static String EXCHANGE_NCDEX = "NCDEX";
    public static String EXCHANGE_MCX = "MCX";

    public static  String ORDER_API_CALL_SUCCESS = "APICallSuccess";
    public static  String ORDER_API_CALL_ERROR = "APICallError";

    /**
     * LTP QUOTE SNAPQUOTE Constants
     */

    public static final int SEQUENCE_NUMBER_OFFSET = 27;
    public static final int EXCHANGE_FEED_TIME_OFFSET = 35;
    public static final int LAST_TRADED_PRICE_OFFSET = 43;
    public static final int SUBSCRIPTION_MODE = 0;
    public static final int EXCHANGE_TYPE = 1;
    public static final int LAST_TRADED_QTY_OFFSET = 51;
    public static final int AVG_TRADED_PRICE_OFFSET = 59;
    public static final int VOLUME_TRADED_TODAY_OFFSET = 67;
    public static final int TOTAL_BUY_QTY_OFFSET = 75;
    public static final int TOTAL_SELL_QTY_OFFSET = 83;
    public static final int OPEN_PRICE_OFFSET = 91;
    public static final int HIGH_PRICE_OFFSET = 99;
    public static final int LOW_PRICE_OFFSET = 107;
    public static final int CLOSE_PRICE_OFFSET = 115;
    public static final int LAST_TRADED_TIMESTAMP_OFFSET = 123;
    public static final int OPEN_INTEREST_OFFSET = 131;
    public static final int OPEN_INTEREST_CHANGE_PERC_OFFSET = 139;
    public static final int UPPER_CIRCUIT_OFFSET = 347;
    public static final int LOWER_CIRCUIT_OFFSET = 355;
    public static final int YEARLY_HIGH_PRICE_OFFSET = 363;
    public static final int YEARLY_LOW_PRICE_OFFSET = 371;



    public static final int BUY_START_POSITION = 147;
    public static final int SELL_START_POSITION = 247;
    public static final int NUM_PACKETS = 5;
    public static final int PACKET_SIZE = 20;
    public static final int BUY_SELL_FLAG_OFFSET = 0;
    public static final int QUANTITY_OFFSET = 2;
    public static final int PRICE_OFFSET = 10;
    public static final int NUMBER_OF_ORDERS_OFFSET = 18;
    public static final int PRICE_CONVERSION_FACTOR = 100;


    public static final String SMART_API_EXCEPTION_ERROR_MSG = "The operation failed to execute because of a SmartAPIException error";
    public static final String IO_EXCEPTION_ERROR_MSG = "The operation failed to execute because of an IO error.";
    public static final String JSON_EXCEPTION_ERROR_MSG = "The operation failed to execute because of a JSON error";
    public static final String SMART_API_EXCEPTION_OCCURRED = "SmartAPIException occurred ";
    public static final String IO_EXCEPTION_OCCURRED = "IOException occurred ";
    public static final String JSON_EXCEPTION_OCCURRED = "JSONException occurred ";

    /**
     * Depth Constants
     */
    public static final int NUM_PACKETS_FOR_DEPTH = 20;
    public static final int PACKET_SIZE_FOR_DEPTH20 = 10;
    public static final int EXCHANGE_TIMESTAMP_FOR_DEPTH20 = 27;
    public static final int PACKET_RECEIVED_TIME_FOR_DEPTH20 = 35;
    public static final int BEST_TWENTY_BUY_DATA_POSITION = 43;
    public static final int BEST_TWENTY_SELL_DATA_POSITION = 243;
    public static final int QUANTITY_OFFSET_FOR_DEPTH20 = 0;
    public static final int PRICE_OFFSET_FOR_DEPTH20 = 4;
    public static final int NUMBER_OF_ORDERS_OFFSET_FOR_DEPTH20 = 8;
    public static final String TOKEN_EXCEPTION_MESSAGE = "Unauthorized access. Please provide a valid or non-expired jwtToken.";
    public static final String APIKEY_EXCEPTION_MESSAGE = "Invalid or missing api key. Please provide a valid api key.";

    /** Margin data */

    public static final String TRADETYPE_BUY = "BUY";

    public static final String TRADETYPE_SELL = "SELL";

}
