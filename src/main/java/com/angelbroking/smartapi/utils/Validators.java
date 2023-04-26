package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

import static com.angelbroking.smartapi.utils.Constants.DISCLOSED_QTY;
import static com.angelbroking.smartapi.utils.Constants.DURATION;
import static com.angelbroking.smartapi.utils.Constants.EXCHANGE;
import static com.angelbroking.smartapi.utils.Constants.ORDER_TYPE;
import static com.angelbroking.smartapi.utils.Constants.PRICE;
import static com.angelbroking.smartapi.utils.Constants.PRODUCT_TYPE;
import static com.angelbroking.smartapi.utils.Constants.QTY;
import static com.angelbroking.smartapi.utils.Constants.QUANTITY;
import static com.angelbroking.smartapi.utils.Constants.SYMBOL_TOKEN;
import static com.angelbroking.smartapi.utils.Constants.TIME_PERIOD;
import static com.angelbroking.smartapi.utils.Constants.TRADING_SYMBOL;
import static com.angelbroking.smartapi.utils.Constants.TRANSACTION_TYPE;
import static com.angelbroking.smartapi.utils.Constants.TRIGGER_PRICE;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.getExchange(), EXCHANGE);
        validateNotEmpty(orderParams.getTradingSymbol(), TRADING_SYMBOL);
        validateNotEmpty(orderParams.getTransactionType(), TRANSACTION_TYPE);
        validateGreaterThanZero(orderParams.getQuantity(), QUANTITY);
        validateGreaterThanZeroDouble(orderParams.getPrice(), PRICE);
        validateNotEmpty(orderParams.getProductType(), PRODUCT_TYPE);
        validateNotEmpty(orderParams.getOrderType(), ORDER_TYPE);
        validateNotEmpty(orderParams.getDuration(), DURATION);
        validateNotEmpty(orderParams.getSymbolToken(), SYMBOL_TOKEN);
        return true;
    }


    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.getTradingSymbol(), TRADING_SYMBOL);
        validateNotEmpty(gttParams.getSymbolToken(), SYMBOL_TOKEN);
        validateNotEmpty(gttParams.getExchange(), EXCHANGE);
        validateNotEmpty(gttParams.getTransactionType(), TRANSACTION_TYPE);
        validateNotEmpty(gttParams.getProductType(), PRODUCT_TYPE);
        validateGreaterThanZeroDouble(gttParams.getPrice(), PRICE);
        validateGreaterThanZero(gttParams.getQty(), QTY);
        validateGreaterThanZeroDouble(gttParams.getTriggerPrice(), TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.getDisclosedQty(), DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.getTimePeriod(), TIME_PERIOD);
        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.getSymbolToken(), SYMBOL_TOKEN);
        validateNotEmpty(gttParams.getExchange(), EXCHANGE);
        validateGreaterThanZeroDouble(gttParams.getPrice(), PRICE);
        validateGreaterThanZero(gttParams.getQty(), QTY);
        validateGreaterThanZeroDouble(gttParams.getTriggerPrice(), TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.getDisclosedQty(), DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.getTimePeriod(), TIME_PERIOD);
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.getExchange(), EXCHANGE);
        validateNotEmpty(orderParams.getTradingSymbol(), TRADING_SYMBOL);
        validateNotEmpty(orderParams.getSymbolToken(), SYMBOL_TOKEN);
        validateGreaterThanZero(orderParams.getQuantity(), QUANTITY);
        validateGreaterThanZeroDouble(orderParams.getPrice(), PRICE);
        validateNotEmpty(orderParams.getProductType(), PRODUCT_TYPE);
        validateNotEmpty(orderParams.getOrderType(), ORDER_TYPE);
        validateNotEmpty(orderParams.getDuration(), DURATION);
        return true;
    }

    public void validateGreaterThanZero(Integer value, String fieldName) throws InvalidParamsException {
        if (value == null || value <= 0) {
            throw new InvalidParamsException(String.format("%s must be greater than zero", fieldName));
        }
    }

    public void validateGreaterThanZeroDouble(Double value, String fieldName) throws InvalidParamsException {
        if (value == null || value <= 0) {
            throw new InvalidParamsException(String.format("%s must be greater than zero", fieldName));
        }
    }

    public void validateNotEmpty(String value, String fieldName) throws InvalidParamsException {
        if (value == null || value.isEmpty()) {
            throw new InvalidParamsException(String.format("%s is required", fieldName));
        }
    }



}
