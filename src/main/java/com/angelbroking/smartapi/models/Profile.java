package com.angelbroking.smartapi.models;

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

    public String email;

    public String userName;

    public String broker;

    public String[] exchanges;

    public String[] products;


}
