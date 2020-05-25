package com.wireguard.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserStore {
    private static UserStore instance;
    private SharedPreferences sharedPreferences;

    private static final String USER_SHARED_PREF = "com.wireguard.android.util.bubbleUserSharedPref";
    private static final String USER_DATA_KEY = "com.wireguard.android.util.bubbleUserResponse";
    public  static final String USER_TOKEN_DEFAULT_VALUE = "";

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

}
