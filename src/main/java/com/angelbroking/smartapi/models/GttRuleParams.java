package com.angelbroking.smartapi.models;

import lombok.Data;

import java.util.List;

@Data
public class GttRuleParams {

    private List<String> status;
    private Integer page;
    private Integer count;


}
