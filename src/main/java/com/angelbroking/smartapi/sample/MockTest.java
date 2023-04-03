package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.Routes;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MockTest {

    public static void main(String[] args) {

         Routes routes = new Routes();

          String clientId = "-";
          String jwtToken = "-";
          String apiKey = "-";

        String swsuri = routes.getSWsuri() + "?jwttoken=" + jwtToken + "&&clientcode=" + clientId + "&&apikey="
                + apiKey;

        StringBuilder sb = new StringBuilder();
        sb.append(routes.getSWsuri())
                .append("?jwttoken=")
                .append(jwtToken)
                .append("&&clientcode=")
                .append(clientId)
                .append("&&apikey=")
                .append(apiKey);

        String suri = sb.toString();
        System.out.println("new: "+suri);
        System.out.println("old: "+swsuri);

    }



}
