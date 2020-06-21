package com.getbubblenow.android.api.network;

import com.getbubblenow.android.model.Device;
import com.getbubblenow.android.model.Network;
import com.getbubblenow.android.model.Sages;
import com.getbubblenow.android.model.User;
import com.getbubblenow.android.api.ApiConstants;
import com.getbubblenow.android.model.Device;
import com.getbubblenow.android.model.User;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


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

    @GET(ApiConstants.CERTIFICATE_URL)
    Single<ResponseBody> getCertificate();

    @GET(ApiConstants.CONFIG_DEVICE_URL+"{deviceID}"+ApiConstants.CONFIG_VPN_URL)
    Single<ResponseBody> getConfig(@Path("deviceID") String deviceID , @HeaderMap HashMap<String,String> header);

    @GET(ApiConstants.BOOTSTRAP_URL_SUFFIX)
    Single<Sages> getSages();

    @GET(ApiConstants.NODE_BASE_URI)
    Single<List<Network>> getNodeBaseURI(@HeaderMap HashMap<String,String> header);
}
