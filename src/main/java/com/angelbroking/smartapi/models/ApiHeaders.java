package com.angelbroking.smartapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiHeaders {

    private String headerClientLocalIP;
    private String headerClientPublicIP;
    private String macAddress;
    private String accept;
    private String userType;
    private String sourceID;

}
