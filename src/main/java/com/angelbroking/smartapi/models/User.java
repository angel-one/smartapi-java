package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User [");
        sb.append("userName=").append(userName).append(", ");
        sb.append("userId=").append(userId).append(", ");
        sb.append("mobileNo=").append(mobileNo).append(", ");
        sb.append("brokerName=").append(brokerName).append(", ");
        sb.append("email=").append(email).append(", ");
        sb.append("lastLoginTime=").append(lastLoginTime).append(", ");
        sb.append("accessToken=").append(accessToken).append(", ");
        sb.append("refreshToken=").append(refreshToken).append(", ");
        sb.append("products=").append(Arrays.toString(products)).append(", ");
        sb.append("exchanges=").append(Arrays.toString(exchanges)).append(", ");
        sb.append("feedToken=").append(feedToken).append("]");
        return sb.toString();
    }


}
