package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, "Exchange");
        validateNotEmpty(orderParams.tradingSymbol, "Trading symbol");
        validateNotEmpty(orderParams.transactionType, "Transaction type");
        validateGreaterThanZero(orderParams.quantity, "Quantity");
        validateGreaterThanZeroDouble(orderParams.price, "Price");
        validateNotEmpty(orderParams.productType, "Product type");
        validateNotEmpty(orderParams.orderType, "Order type");
        validateNotEmpty(orderParams.duration, "Duration");
        validateNotEmpty(orderParams.symbolToken, "Symbol token");
        validateNotEmpty(orderParams.squareOff, "Square off value");
        validateNotEmpty(orderParams.stopLoss, "Stop loss value");
        validateNotEmpty(orderParams.triggerPrice, "Trigger price");

        return true;
    }


    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.tradingSymbol, "Trading symbol");
        validateNotEmpty(gttParams.symbolToken, "Symbol token");
        validateNotEmpty(gttParams.exchange, "Exchange");
        validateNotEmpty(gttParams.transactionType, "Transaction type");
        validateNotEmpty(gttParams.productType, "Product type");
        validateGreaterThanZeroDouble(gttParams.price, "Price");
        validateGreaterThanZero(gttParams.qty, "Quantity");
        validateGreaterThanZeroDouble(gttParams.triggerPrice, "Trigger price");
        validateGreaterThanZero(gttParams.disclosedQty, "Disclosed quantity");
        validateGreaterThanZero(gttParams.timePeriod, "Time period");

        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        validateNotEmpty(gttParams.symbolToken, "Symbol token");
        validateNotEmpty(gttParams.exchange, "Exchange");
        validateGreaterThanZeroDouble(gttParams.price, "Price");
        validateGreaterThanZero(gttParams.qty, "Quantity");
        validateGreaterThanZeroDouble(gttParams.triggerPrice, "Trigger price");
        validateGreaterThanZero(gttParams.disclosedQty, "Disclosed quantity");
        validateGreaterThanZero(gttParams.timePeriod, "Time period");
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        validateNotEmpty(orderParams.exchange, "Exchange");
        validateNotEmpty(orderParams.tradingSymbol, "Trading symbol");
        validateNotEmpty(orderParams.symbolToken, "Symbol token");
        validateGreaterThanZero(orderParams.quantity, "Quantity");
        validateGreaterThanZeroDouble(orderParams.price, "Price");
        validateNotEmpty(orderParams.productType, "Product type");
        validateNotEmpty(orderParams.orderType, "Order type");
        validateNotEmpty(orderParams.duration, "Duration");
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
