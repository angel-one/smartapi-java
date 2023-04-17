package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.getExchange(), Constants.EXCHANGE);
        validateNotEmpty(orderParams.getTradingSymbol(), Constants.TRADING_SYMBOL);
        validateNotEmpty(orderParams.getTransactionType(), Constants.TRANSACTION_TYPE);
        validateGreaterThanZero(orderParams.getQuantity(), Constants.QUANTITY);
        validateGreaterThanZeroDouble(orderParams.getPrice(), Constants.PRICE);
        validateNotEmpty(orderParams.getProductType(), Constants.PRODUCT_TYPE);
        validateNotEmpty(orderParams.getOrderType(), Constants.ORDER_TYPE);
        validateNotEmpty(orderParams.getDuration(), Constants.DURATION);
        validateNotEmpty(orderParams.getSymbolToken(), Constants.SYMBOL_TOKEN);
        validateNotEmpty(orderParams.getSquareOff(), Constants.SQUARE_OFF);
        validateNotEmpty(orderParams.getStopLoss(), Constants.STOP_LOSS);
        return true;
    }


    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.getTradingSymbol(), Constants.TRADING_SYMBOL);
        validateNotEmpty(gttParams.getSymbolToken(), Constants.SYMBOL_TOKEN);
        validateNotEmpty(gttParams.getExchange(), Constants.EXCHANGE);
        validateNotEmpty(gttParams.getTransactionType(), Constants.TRANSACTION_TYPE);
        validateNotEmpty(gttParams.getProductType(), Constants.PRODUCT_TYPE);
        validateGreaterThanZeroDouble(gttParams.getPrice(), Constants.PRICE);
        validateGreaterThanZero(gttParams.getQty(), Constants.QTY);
        validateGreaterThanZeroDouble(gttParams.getTriggerPrice(), Constants.TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.getDisclosedQty(), Constants.DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.getTimePeriod(), Constants.TIME_PERIOD);
        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.getSymbolToken(), Constants.SYMBOL_TOKEN);
        validateNotEmpty(gttParams.getExchange(), Constants.EXCHANGE);
        validateGreaterThanZeroDouble(gttParams.getPrice(), Constants.PRICE);
        validateGreaterThanZero(gttParams.getQty(), Constants.QTY);
        validateGreaterThanZeroDouble(gttParams.getTriggerPrice(), Constants.TRIGGER_PRICE);
        validateGreaterThanZero(gttParams.getDisclosedQty(), Constants.DISCLOSED_QTY);
        validateGreaterThanZero(gttParams.getTimePeriod(), Constants.TIME_PERIOD);
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.getExchange(), Constants.EXCHANGE);
        validateNotEmpty(orderParams.getTradingSymbol(), Constants.TRADING_SYMBOL);
        validateNotEmpty(orderParams.getSymbolToken(), Constants.SYMBOL_TOKEN);
        validateGreaterThanZero(orderParams.getQuantity(), Constants.QUANTITY);
        validateGreaterThanZeroDouble(orderParams.getPrice(), Constants.PRICE);
        validateNotEmpty(orderParams.getProductType(), Constants.PRODUCT_TYPE);
        validateNotEmpty(orderParams.getOrderType(), Constants.ORDER_TYPE);
        validateNotEmpty(orderParams.getDuration(), Constants.DURATION);
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
