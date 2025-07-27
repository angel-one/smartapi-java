package com.angelbroking.smartapi.smartstream.models;

// import lombok.*; // Commented out as we are adding manual setters/getters

// @Setter // Lombok annotation
// @Getter // Lombok annotation
// @NoArgsConstructor // Lombok annotation
// @AllArgsConstructor // Lombok annotation
// @ToString // Lombok annotation
public class BestTwentyData {
    private long quantity = -1;
    private long price = -1;
    private short numberOfOrders = -1;

    // Manual No-Argument Constructor (equivalent to @NoArgsConstructor)
    public BestTwentyData() {
    }

    // Manual All-Arguments Constructor (equivalent to @AllArgsConstructor)
    public BestTwentyData(long quantity, long price, short numberOfOrders) {
        this.quantity = quantity;
        this.price = price;
        this.numberOfOrders = numberOfOrders;
    }

    // Manual Getters (equivalent to @Getter)
    public long getQuantity() {
        return quantity;
    }

    public long getPrice() {
        return price;
    }

    public short getNumberOfOrders() {
        return numberOfOrders;
    }

    // Manual Setters (equivalent to @Setter) - these are called from ByteUtils.java
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setNumberOfOrders(short numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    // Manual toString (equivalent to @ToString) - optional, but good for debugging
    @Override
    public String toString() {
        return "BestTwentyData{" +
               "quantity=" + quantity +
               ", price=" + price +
               ", numberOfOrders=" + numberOfOrders +
               '}';
    }
}
