package com.wireguard.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("related")
    @Expose
    private String related;
    @SerializedName("ctime")
    @Expose
    private long ctime;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("deviceType")
    @Expose
    private String deviceType;
    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("network")
    @Expose
    private String network;
    @SerializedName("shortId")
    @Expose
    private String shortId;

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setRelated(final String related) {
        this.related = related;
    }

    public void setCtime(final long ctime) {
        this.ctime = ctime;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setNetwork(final String network) {
        this.network = network;
    }

    public void setShortId(final String shortId) {
        this.shortId = shortId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRelated() {
        return related;
    }

    public long getCtime() {
        return ctime;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getNetwork() {
        return network;
    }

    public String getShortId() {
        return shortId;
    }
}
