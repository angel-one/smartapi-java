package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartConnectAuthDTO {

    @SerializedName("clientcode")
    private String clientCode;
    private String password;
    private String totp;
}
