package com.wireguard.android.api.network;

import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.model.Device;
import com.wireguard.android.model.User;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;


/**
  Interface for API Calls
 **/
public interface ClientApi {

    @POST(ApiConstants.LOGIN_URL)
    Single<User> login(@Body HashMap<String,String> params);

    @GET(ApiConstants.ALL_DEVICES_URL)
    Single<List<Device>> getAllDevices(@HeaderMap HashMap<String,String> header);

    @PUT(ApiConstants.ADD_DEVICE_URL)
    Single<Device> addDevice(@HeaderMap HashMap<String,String> header , @Body HashMap<String,String> body);
}
