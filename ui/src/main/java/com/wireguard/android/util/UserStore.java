package com.wireguard.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserStore {
    private static UserStore instance;
    private SharedPreferences sharedPreferences;

    public static final String USER_SHARED_PREF = "com.wireguard.android.util.bubbleUserSharedPref";
    private static final String USER_DATA_KEY = "com.wireguard.android.util.bubbleUserResponse";
    private static final String DEVICE_DATA_KEY = "com.wireguard.android.util.bubbleDeviceResponse";
    private static final String DEVICE_ID_KEY = "com.wireguard.android.util.bubbleDeviceIDResponse";
    private static final String USER_BASE_URL_KEY = "com.wireguard.android.util.bubbleUserURLResponse";
    private static final String HOSTNAME_KEY = "com.wireguard.android.util.bubbleHostnameResponse";
    public  static final String USER_TOKEN_DEFAULT_VALUE = "";
    public static final String DEVICE_DEFAULT_VALUE = "";
    public static final String DEVICE_ID_DEFAULT_VALUE = "";
    public static final String USER_BASE_URL_DEFAULT_VALUE = "";
    private static final String HOSTNAME_DEFAULT_VALUE = "";

    public static UserStore getInstance(Context context) {
        if (instance == null) {
            synchronized (UserStore.class) {
                if (instance == null) {
                    instance = new UserStore(context);
                }
            }
        }

        return instance;
    }

    private UserStore(Context context) {
        sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void setToken(String response) {
        sharedPreferences.edit().putString(USER_DATA_KEY, response).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(USER_DATA_KEY, USER_TOKEN_DEFAULT_VALUE);
    }

    public void setDevice(String deviceName, String deviceID){
        sharedPreferences.edit().putString(DEVICE_DATA_KEY, deviceName).apply();
        sharedPreferences.edit().putString(DEVICE_ID_KEY,deviceID).apply();
    }

    public String getDeviceName(){
        return sharedPreferences.getString(DEVICE_DATA_KEY, DEVICE_DEFAULT_VALUE);
    }

    public String getDeviceID(){
        return sharedPreferences.getString(DEVICE_ID_KEY,DEVICE_ID_DEFAULT_VALUE);
    }

    public void setUserURL(String url){
        sharedPreferences.edit().putString(USER_BASE_URL_KEY,url).apply();
    }

    public String getUserURL(){
        return sharedPreferences.getString(USER_BASE_URL_KEY,USER_BASE_URL_DEFAULT_VALUE);
    }

    public void setHostname(String hostName){
        sharedPreferences.edit().putString(HOSTNAME_KEY,hostName).apply();
    }

    public String getHostname(){
        return sharedPreferences.getString(HOSTNAME_KEY,HOSTNAME_DEFAULT_VALUE);
    }
}
