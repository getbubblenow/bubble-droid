package com.getbubblenow.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("related")
    @Expose
    private String related;
    @SerializedName("children")
    @Expose
    private String children;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("parent")
    @Expose
    private String parent;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("admin")
    @Expose
    private boolean admin;
    @SerializedName("sage")
    @Expose
    private boolean sage;
    @SerializedName("suspended")
    @Expose
    private boolean suspended;
    @SerializedName("locked")
    @Expose
    private boolean locked;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("lastLogin")
    @Expose
    private long lastLogin;
    @SerializedName("firstLogin")
    @Expose
    private String firstLogin;
    @SerializedName("termsAgreed")
    @Expose
    private long termsAgreed;
    @SerializedName("preferredPlan")
    @Expose
    private String preferredPlan;
    @SerializedName("autoUpdatePolicy")
    @Expose
    private AutoUpdatePolicy autoUpdatePolicy;
    @SerializedName("promoError")
    @Expose
    private String promoError;
    @SerializedName("apiToken")
    @Expose
    private String apiToken;
    @SerializedName("policy")
    @Expose
    private String policy;
    @SerializedName("sendWelcomeEmail")
    @Expose
    private String sendWelcomeEmail;
    @SerializedName("loginRequest")
    @Expose
    private String loginRequest;
    @SerializedName("multifactorAuth")
    @Expose
    private String multifactorAuth;
    @SerializedName("remoteHost")
    @Expose
    private String remoteHost;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("shortId")
    @Expose
    private String shortId; //27

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setRelated(final String related) {
        this.related = related;
    }

    public void setChildren(final String children) {
        this.children = children;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setParent(final String parent) {
        this.parent = parent;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public void setSage(final boolean sage) {
        this.sage = sage;
    }

    public void setSuspended(final boolean suspended) {
        this.suspended = suspended;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public void setDeleted(final String deleted) {
        this.deleted = deleted;
    }

    public void setLastLogin(final long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setFirstLogin(final String firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void setTermsAgreed(final long termsAgreed) {
        this.termsAgreed = termsAgreed;
    }

    public void setPreferredPlan(final String preferredPlan) {
        this.preferredPlan = preferredPlan;
    }

    public void setAutoUpdatePolicy(final AutoUpdatePolicy autoUpdatePolicy) {
        this.autoUpdatePolicy = autoUpdatePolicy;
    }

    public void setPromoError(final String promoError) {
        this.promoError = promoError;
    }

    public void setApiToken(final String apiToken) {
        this.apiToken = apiToken;
    }

    public void setPolicy(final String policy) {
        this.policy = policy;
    }

    public void setSendWelcomeEmail(final String sendWelcomeEmail) {
        this.sendWelcomeEmail = sendWelcomeEmail;
    }

    public void setLoginRequest(final String loginRequest) {
        this.loginRequest = loginRequest;
    }

    public void setMultifactorAuth(final String multifactorAuth) {
        this.multifactorAuth = multifactorAuth;
    }

    public void setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setToken(final String token) {
        this.token = token;
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

    public String getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isSage() {
        return sage;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getDeleted() {
        return deleted;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public long getTermsAgreed() {
        return termsAgreed;
    }

    public String getPreferredPlan() {
        return preferredPlan;
    }

    public AutoUpdatePolicy getAutoUpdatePolicy() {
        return autoUpdatePolicy;
    }

    public String getPromoError() {
        return promoError;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getPolicy() {
        return policy;
    }

    public String getSendWelcomeEmail() {
        return sendWelcomeEmail;
    }

    public String getLoginRequest() {
        return loginRequest;
    }

    public String getMultifactorAuth() {
        return multifactorAuth;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getToken() {
        return token;
    }

    public String getShortId() {
        return shortId;
    }
}
