package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * A wrapper for user and session details.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @SerializedName("name")
    private String userName;

    @SerializedName("clientcode")
    private String userId;

    @SerializedName("mobileno")
    private String mobileNo;

    @SerializedName("broker")
    private String brokerName;

    private String email;

    @SerializedName("lastlogintime")
    private Date lastLoginTime;

    private String accessToken;

    private String refreshToken;

    private String[] products;
    private String[] exchanges;

    private String feedToken;

}
