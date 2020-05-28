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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

public class DataRepository {
    private static volatile DataRepository instance;
    private ClientApi clientApi;
    private CompositeDisposable compositeDisposable;

    public static final String NO_INTERNET_CONNECTION = "no_internet_connection";

    private DataRepository() {
        clientApi = ClientService.getInstance().createClientApi();
        compositeDisposable = new CompositeDisposable();
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

    public MutableLiveData<StatusResource<User>> login(String username, String password, Context context) {
        return new NetworkBoundStatusResource<User>() {

            @Override protected void createCall() {
                HashMap<String, String> data = new HashMap<>();
                data.put(ApiConstants.USERNAME, username);
                data.put(ApiConstants.PASSWORD, password);
                Disposable disposable = clientApi.login(data)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            UserStore.getInstance(context).setToken(user.getToken());
                            if (!isDeviceLoggedIn(context)) {
                                addDevice(context);
                            }
                            else {
                             getAllDevices(context);
                            }
                        }, throwable -> {
                            setMutableLiveData(StatusResource.error(throwable.getMessage()));
                        });
                compositeDisposable.add(disposable);
            }

            private void getAllDevices(final Context context) {
                final String token = UserStore.getInstance(context).getToken();
                final HashMap<String,String> header = new HashMap<>();
                header.put(ApiConstants.AUTHORIZATION_HEADER,token);
                Disposable disposable1 =  clientApi.getAllDevices(header)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listDevices->{
                            boolean flag = true;
                            for (Device item : listDevices) {
                                if (UserStore.getInstance(context).getDeviceID().equals(item.getUuid())) {
                                    setMutableLiveData(StatusResource.success());
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag) {
                                addDevice(context);
                            }
                        },throwable -> {

                        });
                compositeDisposable.add(disposable1);
            }

            private void addDevice(final Context context) {
                final String brand = getBrand();
                final String model = getDeviceModel();
                final String imei = getDeviceID(context);
                final String deviceName = brand + " " + model + " " + ":" + " " + imei;
                final String token = UserStore.getInstance(context).getToken();
                final HashMap<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTHORIZATION_HEADER, token);
                final Disposable disposable1 = clientApi.getAllDevices(header)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listDevices -> {
                            String brandModel = brand + " " + model + " ";
                            final List<Device> list = listDevices;
                            final List<String> arrayListDevicesName = new ArrayList<>();
                            boolean flag = true;
                            for (final Device device : list) {
                                final String[] deviceNameItem = device.getName().split(":");
                                final String[] myDeviceName = deviceName.split(":");
                                if (deviceNameItem.length > 1) {
                                    if (deviceNameItem[0].contains(myDeviceName[0]) && deviceNameItem[1].contains(myDeviceName[1])) {
                                        UserStore.getInstance(context).setDeviceName(device.getName());
                                        UserStore.getInstance(context).setDeviceID(device.getUuid());
                                        flag = false;
                                        setMutableLiveData(StatusResource.success());
                                        break;
                                    } else {
                                        final String[] itemDevice = device.getName().split(":");
                                        if (itemDevice.length != 1) {
                                            if (itemDevice[0].contains(brandModel)) {
                                                arrayListDevicesName.add(itemDevice[0]);
                                            }
                                        }
                                    }

                                }
                            }
                            if (flag) {
                                if (arrayListDevicesName.isEmpty()) {
                                    brandModel = deviceName;
                                    final HashMap<String, String> body = new HashMap<>();
                                    body.put(ApiConstants.DEVICE_NAME, brandModel);
                                    body.put(ApiConstants.DEVICE_TYPE, "android");
                                    final Disposable disposable2 = clientApi.addDevice(header, body)
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(device -> {
                                                UserStore.getInstance(context).setDeviceName(device.getName());
                                                UserStore.getInstance(context).setDeviceID(device.getUuid());
                                                setMutableLiveData(StatusResource.success());
                                            }, throwable -> {
                                                setMutableLiveData(StatusResource.error(throwable.getMessage()));
                                            });
                                    compositeDisposable.add(disposable2);
                                }
                                else {
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
                                        Disposable disposable2 =    clientApi.addDevice(header,body)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(device -> {
                                                    UserStore.getInstance(context).setDeviceName(device.getName());
                                                    UserStore.getInstance(context).setDeviceID(device.getUuid());
                                                    setMutableLiveData(StatusResource.success());
                                                },throwable -> {
                                                    setMutableLiveData(StatusResource.error(throwable.getMessage()));
                                                });
                                        compositeDisposable.add(disposable2);
                                    }
                                }
                            }
                        }, throwable -> {
                            setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                        });
                compositeDisposable.add(disposable1);
            }
        }.getMutableLiveData();
    }

    private boolean isDeviceLoggedIn(Context context) {
        return !UserStore.DEVICE_DEFAULT_VALUE.equals(UserStore.getInstance(context).getDeviceName());
    }

    private String createErrorMessage(Call call, retrofit2.Response response) {
        return "Error: User agent: " + System.getProperty("http.agent") + ", Request body: " + call.request().body() + ", URL: " +
                call.request().url() + ", Code: " + response.code() + ", Message: " +
                response.message();
    }

    public boolean isUserLoggedIn(Context context) {
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

    private String getDeviceID(final Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private String getDeviceModel() {
        return Build.MODEL;
    }

    public void clearDisposable(){
        compositeDisposable.clear();
    }
}
