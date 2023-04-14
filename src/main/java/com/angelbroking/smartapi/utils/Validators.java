package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, Constants.EXCHANGE);
        validateNotEmpty(orderParams.tradingSymbol, Constants.TRADING_SYMBOL);
        validateNotEmpty(orderParams.transactionType, Constants.TRANSACTION_TYPE);
        validateGreaterThanZero(orderParams.quantity, Constants.QUANTITY);
        validateGreaterThanZeroDouble(orderParams.price, Constants.PRICE);
        validateNotEmpty(orderParams.productType, Constants.PRODUCT_TYPE);
        validateNotEmpty(orderParams.orderType, Constants.ORDER_TYPE);
        validateNotEmpty(orderParams.duration, Constants.DURATION);
        validateNotEmpty(orderParams.symbolToken, Constants.SYMBOL_TOKEN);
        validateNotEmpty(orderParams.squareOff, Constants.SQUARE_OFF);
        validateNotEmpty(orderParams.stopLoss, Constants.STOP_LOSS);

        return true;
    }


    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.tradingSymbol, Constants.TRADING_SYMBOL);
        validateNotEmpty(gttParams.symbolToken, Constants.SYMBOL_TOKEN);
        validateNotEmpty(gttParams.exchange, Constants.EXCHANGE);
        validateNotEmpty(gttParams.transactionType, Constants.TRANSACTION_TYPE);
        validateNotEmpty(gttParams.productType, Constants.PRODUCT_TYPE);
        validateGreaterThanZeroDouble(gttParams.price, Constants.PRICE);
        validateGreaterThanZero(gttParams.qty, Constants.QTY);
        validateGreaterThanZeroDouble(gttParams.triggerPrice, Constants.TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.disclosedQty, Constants.DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.timePeriod, Constants.TIME_PERIOD);

        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.symbolToken, Constants.SYMBOL_TOKEN);
        validateNotEmpty(gttParams.exchange, Constants.EXCHANGE);
        validateGreaterThanZeroDouble(gttParams.price, Constants.PRICE);
        validateGreaterThanZero(gttParams.qty, Constants.QTY);
        validateGreaterThanZeroDouble(gttParams.triggerPrice, Constants.TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.disclosedQty, Constants.DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.timePeriod, Constants.TIME_PERIOD);
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, Constants.EXCHANGE);
        validateNotEmpty(orderParams.tradingSymbol, Constants.TRADING_SYMBOL);
        validateNotEmpty(orderParams.symbolToken, Constants.SYMBOL_TOKEN);
        validateGreaterThanZero(orderParams.quantity, Constants.QUANTITY);
        validateGreaterThanZeroDouble(orderParams.price, Constants.PRICE);
        validateNotEmpty(orderParams.productType, Constants.PRODUCT_TYPE);
        validateNotEmpty(orderParams.orderType, Constants.ORDER_TYPE);
        validateNotEmpty(orderParams.duration, Constants.DURATION);
        return true;
    }

    private void validateGreaterThanZero(Integer value, String fieldName) throws InvalidParamsException {
        if (value == null || value <= 0) {
            throw new InvalidParamsException(fieldName + " must be greater than zero");
        }
    }

    private void validateGreaterThanZeroDouble(Double value, String fieldName) throws InvalidParamsException {
        if (value == null || value <= 0) {
            throw new InvalidParamsException(fieldName + " must be greater than zero");
        }
    }

    private void validateNotEmpty(String value, String fieldName) throws InvalidParamsException {
        if (value == null || value.isEmpty()) {
            throw new InvalidParamsException(fieldName + " is required");
        }
    }


}
