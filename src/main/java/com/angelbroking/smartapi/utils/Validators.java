package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;

public class Validators {

    public boolean orderValidator(OrderParams orderParams) throws InvalidParamsException {
        if (orderParams.exchange == null || orderParams.exchange.isEmpty()) {
            throw new InvalidParamsException("Exchange is required");
        }
        if (orderParams.tradingSymbol == null || orderParams.tradingSymbol.isEmpty()) {
            throw new InvalidParamsException("Trading symbol is required");
        }
        if (orderParams.transactionType == null || orderParams.transactionType.isEmpty()) {
            throw new InvalidParamsException("Transaction type is required");
        }
        if (orderParams.quantity == null || orderParams.quantity <= 0) {
            throw new InvalidParamsException("Quantity must be greater than zero");
        }
        if (orderParams.price == null || orderParams.price <= 0) {
            throw new InvalidParamsException("Price must be greater than zero");
        }
        if (orderParams.productType == null || orderParams.productType.isEmpty()) {
            throw new InvalidParamsException("Product type is required");
        }
        if (orderParams.orderType == null || orderParams.orderType.isEmpty()) {
            throw new InvalidParamsException("Order type is required");
        }
        if (orderParams.duration == null || orderParams.duration.isEmpty()) {
            throw new InvalidParamsException("Duration is required");
        }
        if (orderParams.symbolToken == null || orderParams.symbolToken.isEmpty()) {
            throw new InvalidParamsException("Symbol token is required");
        }
        if (orderParams.squareOff != null && orderParams.squareOff.isEmpty()) {
            throw new InvalidParamsException("Square off value is invalid");
        }
        if (orderParams.stopLoss != null && orderParams.stopLoss.isEmpty()) {
            throw new InvalidParamsException("Stop loss value is invalid");
        }
        if (orderParams.triggerPrice != null && orderParams.triggerPrice.isEmpty()) {
            throw new InvalidParamsException("Trigger price is invalid");
        }
        // all required fields are present and valid, so return true
        return true;
    }

    public boolean gttParamsValidator(GttParams gttParams) throws InvalidParamsException {
        if (gttParams.tradingSymbol == null || gttParams.tradingSymbol.isEmpty()) {
            throw new InvalidParamsException("Trading symbol is required");
        }
        if (gttParams.symbolToken == null || gttParams.symbolToken.isEmpty()) {
            throw new InvalidParamsException("Symbol token is required");
        }
        if (gttParams.exchange == null || gttParams.exchange.isEmpty()) {
            throw new InvalidParamsException("Exchange is required");
        }
        if (gttParams.transactionType == null || gttParams.transactionType.isEmpty()) {
            throw new InvalidParamsException("Transaction type is required");
        }
        if (gttParams.productType == null || gttParams.productType.isEmpty()) {
            throw new InvalidParamsException("Product type is required");
        }
        if (gttParams.price == null ) {
            throw new InvalidParamsException("Price is required");
        }
        if (gttParams.qty == null ) {
            throw new InvalidParamsException("Quantity is required");
        }
        if (gttParams.triggerPrice != null) {
            throw new InvalidParamsException("Trigger price is invalid");
        }
        if (gttParams.disclosedQty != null) {
            throw new InvalidParamsException("Disclosed quantity is invalid");
        }
        if (gttParams.timePeriod == null) {
            throw new InvalidParamsException("Time period is required");
        }
        return true;
    }

    public boolean gttModifyRuleValidator(GttParams gttParams) throws InvalidParamsException {
        if (gttParams.symbolToken != null || gttParams.symbolToken.isEmpty()) {
            throw new InvalidParamsException("Symbol token is invalid");
        }
        if (gttParams.exchange != null || gttParams.exchange.isEmpty()) {
            throw new InvalidParamsException("Exchange is invalid");
        }
        if (gttParams.price != null || gttParams.price <= 0) {
            throw new InvalidParamsException("Price should be greater than zero");
        }
        if (gttParams.qty != null || gttParams.qty <= 0) {
            throw new InvalidParamsException("Quantity should be greater than zero");
        }
        if (gttParams.triggerPrice != null || gttParams.triggerPrice <= 0) {
            throw new InvalidParamsException("Trigger price should be greater than zero");
        }
        if (gttParams.disclosedQty != null || gttParams.disclosedQty <= 0) {
            throw new InvalidParamsException("Disclosed quantity should be greater than zero");
        }
        if (gttParams.timePeriod != null ) {
            throw new InvalidParamsException("Time period is invalid");
        }
        return true;
    }

    public boolean modifyOrderValidator(OrderParams orderParams) throws InvalidParamsException {
        if (orderParams.exchange == null || orderParams.exchange.isEmpty()) {
            throw new InvalidParamsException("Exchange is invalid");
        }
        if (orderParams.tradingSymbol == null || orderParams.tradingSymbol.isEmpty()) {
            throw new InvalidParamsException("Trading symbol is invalid");
        }
        if (orderParams.symbolToken == null || orderParams.symbolToken.isEmpty()) {
            throw new InvalidParamsException("Symbol token is invalid");
        }
        if (orderParams.quantity == null || orderParams.quantity <= 0) {
            throw new InvalidParamsException("Quantity is invalid");
        }
        if (orderParams.price == null || orderParams.price <= 0) {
            throw new InvalidParamsException("Price is invalid");
        }
        if (orderParams.productType == null || orderParams.productType.isEmpty()) {
            throw new InvalidParamsException("Product type is invalid");
        }
        if (orderParams.orderType == null || orderParams.orderType.isEmpty()) {
            throw new InvalidParamsException("Order type is invalid");
        }
        if (orderParams.duration == null || orderParams.duration.isEmpty()) {
            throw new InvalidParamsException("Duration is invalid");
        }
        return true;
    }


}
