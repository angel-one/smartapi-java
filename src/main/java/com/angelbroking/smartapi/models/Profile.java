package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A wrapper for profile response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    private String email;

    @SerializedName("name")
    private String userName;

    private String broker;

    private String[] exchanges;

    private String[] products;


}
