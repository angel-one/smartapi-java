package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WsMWRequest {
    private String token;
    private String user;
    @SerializedName("acctid")
    private String acctId;
    private String task;
    private String channel;

    public WsMWRequest(String token, String user, String acctId) {
        this.token = token;
        this.user = user;
        this.acctId = acctId;
    }

    public WsMWRequest(String token, String user, String acctId, String task, String channel) {
        this.token = token;
        this.user = user;
        this.acctId = acctId;
        this.task = task;
        this.channel = channel;
    }
}
