package com.angelbroking.smartapi.models;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeTokenDTO {
    private String exchangeType;
    private List<String> tokens;
}
