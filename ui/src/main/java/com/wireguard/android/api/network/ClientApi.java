package com.wireguard.android.api.network;

import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
  Interface for API Calls
 **/
public interface ClientApi {
    @FormUrlEncoded
    @POST(ApiConstants.LOGIN_URL)
    Call<User> login(@FieldMap Map<String,String> params);
}
