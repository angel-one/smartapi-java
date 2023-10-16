package com.angelbroking.smartapi.smartstream.models;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BestTwentyData {
    private long quantity = -1;
    private long price = -1;
    private short numberOfOrders = -1;
}
