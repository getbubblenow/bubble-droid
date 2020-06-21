package com.getbubblenow.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Network {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("related")
    @Expose
    private String related;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("sslPort")
    @Expose
    private long sslPort;
    @SerializedName("installType")
    @Expose
    private String installType;
    @SerializedName("sshKey")
    @Expose
    private String sshKey;
    @SerializedName("computeSizeType")
    @Expose
    private String computeSizeType;
    @SerializedName("footprint")
    @Expose
    private String footprint;
    @SerializedName("storage")
    @Expose
    private String storage;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("sendMetrics")
    @Expose
    private boolean sendMetrics;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("forkHost")
    @Expose
    private String forkHost;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("shortId")
    @Expose
    private String shortId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(final String related) {
        this.related = related;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    public long getSslPort() {
        return sslPort;
    }

    public void setSslPort(final long sslPort) {
        this.sslPort = sslPort;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(final String installType) {
        this.installType = installType;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(final String sshKey) {
        this.sshKey = sshKey;
    }

    public String getComputeSizeType() {
        return computeSizeType;
    }

    public void setComputeSizeType(final String computeSizeType) {
        this.computeSizeType = computeSizeType;
    }

    public String getFootprint() {
        return footprint;
    }

    public void setFootprint(final String footprint) {
        this.footprint = footprint;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(final String storage) {
        this.storage = storage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public boolean isSendMetrics() {
        return sendMetrics;
    }

    public void setSendMetrics(final boolean sendMetrics) {
        this.sendMetrics = sendMetrics;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public String getForkHost() {
        return forkHost;
    }

    public void setForkHost(final String forkHost) {
        this.forkHost = forkHost;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(final String shortId) {
        this.shortId = shortId;
    }
}
