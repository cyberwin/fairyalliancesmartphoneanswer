package com.fairyalliance.smartanswer;
import com.google.gson.annotations.SerializedName;

public class ContactTagItem {
    @SerializedName("tag")
    public String tag;
    @SerializedName("name")
    public String name;
    @SerializedName("updateTs")
    public long updateTs;
    @SerializedName("phone")
    public String phone;
}