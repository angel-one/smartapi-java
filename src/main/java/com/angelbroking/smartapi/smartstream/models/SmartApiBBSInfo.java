package com.angelbroking.smartapi.smartstream.models;

// You can use Lombok annotations if preferred, and if Lombok is configured correctly.
// import lombok.Data;
// @Data
public class SmartApiBBSInfo {
    // Fields used by mapSnapQuoteResponseToBBSInfo in ByteUtils
    private int packetLength;
    private String token; // Assuming token is a String based on ByteUtils.readString()
    private int sequenceNumber;
    private short exchangeType;

    // Fields used by getBestFiveBuyData and getBestFiveSellData in ByteUtils
    private short buySellFlag;
    private long quantity;
    private long price;
    private short numberOfOrders;
    
    // Constructor for fields from getBestFiveBuyData/getBestFiveSellData if needed,
    // but @Data and @NoArgsConstructor with setters are generally sufficient.
    // Lombok's @Data will generate getters, setters, toString, equals, and hashCode.
    
    // Getters
    public int getPacketLength() {
 return packetLength;
    }

    public String getToken() {
 return token;
    }

    public int getSequenceNumber() {
 return sequenceNumber;
    }

    public short getExchangeType() {
 return exchangeType;
    }

    public short getBuySellFlag() {
 return buySellFlag;
    }

    public long getQuantity() {
 return quantity;
    }

    public long getPrice() {
 return price;
    }

    public short getNumberOfOrders() {
 return numberOfOrders;
    }
    
    // Setters - these are called from ByteUtils.java
    public void setPacketLength(int packetLength) { this.packetLength = packetLength; }
    public void setToken(String token) { this.token = token; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public void setExchangeType(short exchangeType) { this.exchangeType = exchangeType; }
    public void setBuySellFlag(short buySellFlag) { this.buySellFlag = buySellFlag; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
    public void setPrice(long price) { this.price = price; }
    public void setNumberOfOrders(short numberOfOrders) { this.numberOfOrders = numberOfOrders; }
}