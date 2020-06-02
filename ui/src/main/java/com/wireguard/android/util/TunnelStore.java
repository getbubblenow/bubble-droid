package com.wireguard.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TunnelStore {
    private static TunnelStore instance;
    private SharedPreferences sharedPreferences;

    private static final String TUNNEL_SHARED_PREF = "com.wireguard.android.util.bubbleTunnelSharedPref";
    private static final String TUNNEL_DATA_KEY = "com.wireguard.android.util.bubbleResponse";
    private static final String CONFIG_DATA_KEY = "com.wireguard.android.util.bubbleConfigResponse";
    public static final String TUNNEL_DEFAULT_VALUE = "";
    public static final String CONFIG_DEFAULT_VALUE = "";

    public static TunnelStore getInstance(Context context) {
        if (instance == null) {
            synchronized (UserStore.class) {
                if (instance == null) {
                    instance = new TunnelStore(context);
                }
            }
        }

        return instance;
    }

    private TunnelStore(Context context) {
        sharedPreferences = context.getSharedPreferences(TUNNEL_SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void setTunnelName(String tunnelName){
        sharedPreferences.edit().putString(TUNNEL_DATA_KEY,tunnelName).apply();
    }

    public String getTunnelName(){
        return sharedPreferences.getString(TUNNEL_DATA_KEY, TUNNEL_DEFAULT_VALUE);
    }

    public void setConfig(String config){
        sharedPreferences.edit().putString(CONFIG_DATA_KEY,config).apply();
    }

    public String getConfig(){
        return sharedPreferences.getString(CONFIG_DATA_KEY,CONFIG_DEFAULT_VALUE);
    }
}
