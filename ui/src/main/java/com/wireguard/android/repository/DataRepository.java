package com.wireguard.android.repository;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.api.network.ClientApi;
import com.wireguard.android.api.network.ClientService;
import com.wireguard.android.api.network.NetworkBoundStatusResource;
import com.wireguard.android.model.Device;
import com.wireguard.android.model.User;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.util.UserStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {
    private static volatile DataRepository instance;
    private ClientApi clientApi;

    public static final String NO_INTERNET_CONNECTION = "no_internet_connection";

    private DataRepository()
    {
        clientApi = ClientService.getInstance().createClientApi();
    }

    public static void buildRepositoryInstance() {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository();
                }
            }
        }
    }

    public static DataRepository getRepositoryInstance() {
        return instance;
    }

    public MutableLiveData<StatusResource<User>> login(String username,String password , Context context){
        return new NetworkBoundStatusResource<User>(){

            @Override protected void createCall() {
                HashMap<String,String> data = new HashMap<>();
                data.put(ApiConstants.USERNAME,username);
                data.put(ApiConstants.PASSWORD,password);
                clientApi.login(data).enqueue(new Callback<User>() {
                    @Override public void onResponse(final Call<User> call, final Response<User> response) {
                        if(response.isSuccessful()) {
                            String token = response.body().getToken();
                            UserStore.getInstance(context).setToken(token);
                            setMutableLiveData(StatusResource.success());
                        }
                        else {
                            String errorMessage = createErrorMessage(call,response);
                            setMutableLiveData(StatusResource.error(errorMessage));
                        }
                    }

                    @Override public void onFailure(final Call<User> call, final Throwable t) {
                        if(t instanceof Exception){
                            setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                        }
                    }
                });
            }
        }.getMutableLiveData();
    }

    public MutableLiveData<StatusResource<Device>> getAllDevices(Context context) {
        return new NetworkBoundStatusResource<Device>() {
            @Override protected void createCall() {
                final String token = UserStore.getInstance(context).getToken();
                final HashMap<String,String> header = new HashMap<>();
                header.put(ApiConstants.HEADER,token);
                clientApi.getAllDevices(header).enqueue(new Callback<List<Device>>() {
                    @Override public void onResponse(final Call<List<Device>> call, final Response<List<Device>> response) {
                        if (response.isSuccessful()) {
                            List<Device> list = response.body();
                            setMutableLiveData(StatusResource.success());
                        } else {
                            String errorMessage = createErrorMessage(call, response);
                            setMutableLiveData(StatusResource.error(errorMessage));
                        }
                    }

                    @Override public void onFailure(final Call<List<Device>> call, final Throwable t) {
                        if (t instanceof Exception) {
                            setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                        }
                    }
                });
            }
        }.getMutableLiveData();
    }

    public MutableLiveData<StatusResource<Device>> addDevice(Context context) {
        return new NetworkBoundStatusResource<Device>() {
            @Override protected void createCall() {
                        final String brand = getBrand();
                        final String model = getDeviceModel();
                        final String imei = getDeviceIMEI(context);
                        final String deviceName = brand + " " + model + " " + ":" + " " + imei;
                        final String token = UserStore.getInstance(context).getToken();
                        final HashMap<String,String> header = new HashMap<>();
                        header.put(ApiConstants.HEADER,token);
                        clientApi.getAllDevices(header).enqueue(new Callback<List<Device>>() {
                            @Override public void onResponse(final Call<List<Device>> call, final Response<List<Device>> response) {
                                if (response.isSuccessful()) {
                                    String brandModel = brand + " " + model + " ";
                                    final List<Device> list = response.body();
                                    final List<String> arrayListDevicesName = new ArrayList<>();
                                    boolean flag = true;
                                    for (final Device device : list) {
                                        final String[] deviceNameItem = device.getName().split(":");
                                        final String[] myDeviceName = deviceName.split(":");
                                        if(deviceNameItem.length>1){
                                            if(deviceNameItem[0].contains(myDeviceName[0]) && deviceNameItem[1].contains(myDeviceName[1])){
                                                setMutableLiveData(StatusResource.success());
                                                flag = false;
                                                break;
                                            }
                                            else {
                                                final String[] itemDevice = device.getName().split(":");
                                                if (itemDevice.length != 1) {
                                                    if(itemDevice[0].contains(brandModel)) {
                                                        arrayListDevicesName.add(itemDevice[0]);
                                                    }
                                                }
                                            }

                                        }
                                    }
                                    if(flag) {
                                        if (arrayListDevicesName.isEmpty()) {
                                            brandModel = deviceName;
                                            final HashMap<String, String> body = new HashMap<>();
                                            body.put(ApiConstants.DEVICE_NAME, brandModel);
                                            body.put(ApiConstants.DEVICE_TYPE, "android");
                                            clientApi.addDevice(header, body).enqueue(new Callback<Device>() {
                                                @Override public void onResponse(final Call<Device> call, final Response<Device> response) {
                                                    if (response.isSuccessful()) {
                                                        setMutableLiveData(StatusResource.success());
                                                    } else {
                                                        final String errorMessage = createErrorMessage(call, response);
                                                        setMutableLiveData(StatusResource.error(errorMessage));
                                                    }
                                                }

                                                @Override public void onFailure(final Call<Device> call, final Throwable t) {
                                                    if (t instanceof Exception) {
                                                        setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                                                    }
                                                }
                                            });
                                        } else {
                                            for (int i = (arrayListDevicesName.size() - 1); i >= arrayListDevicesName.size() - 1; i--) {
                                                if (arrayListDevicesName.get(i).contains(brandModel)) {
                                                    final char[] arr = arrayListDevicesName.get(i).toCharArray();
                                                    if (arr[arr.length - 2] != ')') {
                                                        brandModel += "(2)" + " " + ":" + " " + imei;
                                                    } else {
                                                        String countDevice = "";
                                                        int indexfirst = 0;
                                                        int indexlast = 0;
                                                        for (int j = arr.length - 1; j >= 0; j--) {
                                                            if (arr[j] == '(') {
                                                                indexfirst = j;
                                                            }
                                                            if (arr[j] == ')') {
                                                                indexlast = j;
                                                            }
                                                        }
                                                        final String device = new String(arr);
                                                        countDevice += device.substring(indexfirst + 1, indexlast);
                                                        int count = Integer.parseInt(countDevice);
                                                        count++;
                                                        brandModel += "(" + count + ")" + " " + ":" + " " + imei;
                                                    }
                                                }
                                                final HashMap<String, String> body = new HashMap<>();
                                                body.put(ApiConstants.DEVICE_NAME, brandModel);
                                                body.put(ApiConstants.DEVICE_TYPE, "android");
                                                clientApi.addDevice(header, body).enqueue(new Callback<Device>() {
                                                    @Override public void onResponse(final Call<Device> call, final Response<Device> response) {
                                                        if (response.isSuccessful()) {
                                                            setMutableLiveData(StatusResource.success());
                                                        } else {
                                                            final String errorMessage = createErrorMessage(call, response);
                                                            setMutableLiveData(StatusResource.error(errorMessage));
                                                        }
                                                    }

                                                    @Override public void onFailure(final Call<Device> call, final Throwable t) {
                                                        if (t instanceof Exception) {
                                                            setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                            @Override public void onFailure(final Call<List<Device>> call, final Throwable t) {
                                if (t instanceof Exception) {
                                    setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                                }
                            }
                        });

                    }
                }.getMutableLiveData();
            }

    private String createErrorMessage(Call call, retrofit2.Response response) {
        return "Error: User agent: " + System.getProperty("http.agent") + ", Request body: " + call.request().body() + ", URL: " +
                call.request().url() + ", Code: " + response.code() + ", Message: " +
                response.message();
    }

    public boolean isUserLoggedIn(Context context)
    {
        return !UserStore.USER_TOKEN_DEFAULT_VALUE.equals(UserStore.getInstance(context).getToken());
    }

    private String getBrand() {
        final String brand = Build.MANUFACTURER;
        return capitalize(brand);
    }


    private String capitalize(final String brand) {
        if (brand == null || brand.isEmpty()) {
            return "";
        }
        final char first = brand.charAt(0);
        return Character.isUpperCase(first) ? brand : Character.toUpperCase(first) + brand.substring(1);
    }

    private String getDeviceIMEI(final Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private String getDeviceModel(){
        return Build.MODEL;
    }
}
