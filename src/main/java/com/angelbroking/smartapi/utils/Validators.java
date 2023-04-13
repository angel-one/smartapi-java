package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, orderParams.exchange);
        validateNotEmpty(orderParams.tradingSymbol, orderParams.tradingSymbol);
        validateNotEmpty(orderParams.transactionType, orderParams.transactionType);
        validateGreaterThanZero(orderParams.quantity, String.valueOf( orderParams.quantity));
        validateGreaterThanZeroDouble(orderParams.price, String.valueOf(orderParams.price));
        validateNotEmpty(orderParams.productType, orderParams.productType);
        validateNotEmpty(orderParams.orderType, orderParams.orderType);
        validateNotEmpty(orderParams.duration, orderParams.duration);
        validateNotEmpty(orderParams.symbolToken, orderParams.symbolToken);
        validateNotEmpty(orderParams.squareOff, orderParams.squareOff);
        validateNotEmpty(orderParams.stopLoss, orderParams.stopLoss);
        validateNotEmpty(orderParams.triggerPrice, orderParams.triggerPrice);

        return true;
    }


    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.tradingSymbol, gttParams.tradingSymbol);
        validateNotEmpty(gttParams.symbolToken, gttParams.symbolToken);
        validateNotEmpty(gttParams.exchange, gttParams.exchange);
        validateNotEmpty(gttParams.transactionType, gttParams.transactionType);
        validateNotEmpty(gttParams.productType, gttParams.productType);
        validateGreaterThanZeroDouble(gttParams.price, String.valueOf(gttParams.price));
        validateGreaterThanZero(gttParams.qty, String.valueOf(gttParams.qty));
        validateGreaterThanZeroDouble(gttParams.triggerPrice, String.valueOf(gttParams.triggerPrice));
        validateGreaterThanZero(gttParams.disclosedQty, String.valueOf(gttParams.disclosedQty));
        validateGreaterThanZero(gttParams.timePeriod, String.valueOf(gttParams.timePeriod));

        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.symbolToken, gttParams.symbolToken);
        validateNotEmpty(gttParams.exchange, gttParams.exchange);
        validateGreaterThanZeroDouble(gttParams.price,  String.valueOf(gttParams.price));
        validateGreaterThanZero(gttParams.qty, String.valueOf(gttParams.qty));
        validateGreaterThanZeroDouble(gttParams.triggerPrice, String.valueOf(gttParams.triggerPrice));
        validateGreaterThanZero(gttParams.disclosedQty, String.valueOf(gttParams.disclosedQty));
        validateGreaterThanZero(gttParams.timePeriod, String.valueOf(gttParams.timePeriod));
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, orderParams.exchange);
        validateNotEmpty(orderParams.tradingSymbol, orderParams.tradingSymbol);
        validateNotEmpty(orderParams.symbolToken, orderParams.symbolToken);
        validateGreaterThanZero(orderParams.quantity, String.valueOf(orderParams.quantity));
        validateGreaterThanZeroDouble(orderParams.price, String.valueOf(orderParams.price));
        validateNotEmpty(orderParams.productType, orderParams.productType);
        validateNotEmpty(orderParams.orderType, orderParams.orderType);
        validateNotEmpty(orderParams.duration, orderParams.duration);
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
