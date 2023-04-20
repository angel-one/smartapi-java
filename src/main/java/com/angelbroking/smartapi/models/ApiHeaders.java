package com.angelbroking.smartapi.models;

public class ApiHeaders {

    private String headerClientLocalIP;
    private String headerClientPublicIP;
    private String macAddress;
    private String accept;
    private String userType;
    private String sourceID;

    public String getHeaderClientLocalIP() {
        return headerClientLocalIP;
    }

    public void setHeaderClientLocalIP(String headerClientLocalIP) {
        this.headerClientLocalIP = headerClientLocalIP;
    }

    public String getHeaderClientPublicIP() {
        return headerClientPublicIP;
    }

    public void setHeaderClientPublicIP(String headerClientPublicIP) {
        this.headerClientPublicIP = headerClientPublicIP;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }
}
