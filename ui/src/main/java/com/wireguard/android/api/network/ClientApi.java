package com.wireguard.android.api.network;

import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.model.Device;
import com.wireguard.android.model.User;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;


/**
  Interface for API Calls
 **/
public interface ClientApi {

    @POST(ApiConstants.LOGIN_URL)
    Call<User> login(@Body HashMap<String,String> params);

    @GET(ApiConstants.ALL_DEVICES_URL)
    Call<List<Device>> getAllDevices(@HeaderMap HashMap<String,String> header);
}
