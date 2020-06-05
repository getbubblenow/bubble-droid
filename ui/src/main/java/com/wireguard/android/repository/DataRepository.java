package com.wireguard.android.repository;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.wireguard.android.Application;
import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.api.network.ClientApi;
import com.wireguard.android.api.network.ClientService;
import com.wireguard.android.api.network.NetworkBoundStatusResource;
import com.wireguard.android.backend.Tunnel.State;
import com.wireguard.android.configStore.FileConfigStore;
import com.wireguard.android.model.Device;
import com.wireguard.android.model.ObservableTunnel;
import com.wireguard.android.model.TunnelManager;
import com.wireguard.android.model.User;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.util.TunnelStore;
import com.wireguard.android.util.UserStore;
import com.wireguard.config.BadConfigException;
import com.wireguard.config.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class DataRepository {
    private static volatile DataRepository instance;
    private ClientApi clientApi;
    private CompositeDisposable compositeDisposable;
    private final OkHttpClient client = new OkHttpClient();
    private TunnelManager tunnelManager;

    public static final String NO_INTERNET_CONNECTION = "no_internet_connection";
    private static final String SEPARATOR = ":";
    private static final String SPACE = " ";
    private static final String DELIMITER = "\\A";
    private static final int ANDROID_ID = 1;
    private static  String BASE_URL = "";
    private static final String TUNNEL_NAME = "BubbleVPN";

    private DataRepository(Context context,String url) {
        BASE_URL = url;
        clientApi = ClientService.getInstance().createClientApi(url);
        compositeDisposable = new CompositeDisposable();
        tunnelManager = new TunnelManager(new FileConfigStore(context));
    }

    public static void buildRepositoryInstance(Context context, String url) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(context,url);
                }
            }
        }
    }

    public void buildClientService(String url){
        BASE_URL = url;
        clientApi = ClientService.getInstance().createClientApi(url);
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
                Disposable disposableLogin = clientApi.login(data)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            UserStore.getInstance(context).setToken(user.getToken());
                            if (!isDeviceLoggedIn(context)) {
                                addDevice(context);
                            } else {
                                getAllDevices(context);
                            }
                        }, throwable -> {
                            setMutableLiveData(StatusResource.error(throwable.getMessage()));
                        });
                compositeDisposable.add(disposableLogin);
            }

            private void getAllDevices(final Context context) {
                final String token = UserStore.getInstance(context).getToken();
                final HashMap<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTHORIZATION_HEADER, token);
                Disposable disposableAllDevices = clientApi.getAllDevices(header)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listDevices -> {
                            boolean hasDevice = false;
                            for (Device item : listDevices) {
                                if (UserStore.getInstance(context).getDeviceID().equals(item.getUuid())) {
                                    setMutableLiveData(StatusResource.success());
                                    hasDevice = true;
                                    break;
                                }
                            }
                            if (!hasDevice) {
                                addDevice(context);
                            }
                        }, throwable -> {
                            setMutableLiveData(StatusResource.error(throwable.getMessage()));
                        });
                compositeDisposable.add(disposableAllDevices);
            }

            private void addDevice(final Context context) {
                final String brand = getBrand();
                final String model = getDeviceModel();
                final String imei = getDeviceID(context);
                final String deviceName = brand + SPACE + model + SPACE + SEPARATOR + SPACE + imei;
                final String token = UserStore.getInstance(context).getToken();
                final HashMap<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTHORIZATION_HEADER, token);
                final Disposable disposableAllDevices = clientApi.getAllDevices(header)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listDevices -> {
                            String brandModel = brand + SPACE + model + SPACE;
                            final List<Device> list = listDevices;
                            final List<String> arrayListDevicesName = new ArrayList<>();
                            boolean hasDevice = false;
                            for (final Device device : list) {
                                final String[] deviceNameItem = device.getName().split(SEPARATOR);
                                final String[] myDeviceName = deviceName.split(SEPARATOR);
                                if (deviceNameItem.length > 1) {
                                    if (deviceNameItem[ANDROID_ID].equals(myDeviceName[ANDROID_ID])) {
                                        UserStore.getInstance(context).setDevice(device.getName(), device.getUuid());
                                        hasDevice = true;
                                        getConfig(context);
                                        break;
                                    } else {
                                        final String[] itemDevice = device.getName().split(SEPARATOR);
                                        if (itemDevice.length != 1) {
                                            if (itemDevice[0].contains(brandModel)) {
                                                arrayListDevicesName.add(itemDevice[0]);
                                            }
                                        }
                                    }

                                }
                            }
                            if (!hasDevice) {
                                if (arrayListDevicesName.isEmpty()) {
                                    brandModel = deviceName;
                                    final HashMap<String, String> body = new HashMap<>();
                                    body.put(ApiConstants.DEVICE_NAME, brandModel);
                                    body.put(ApiConstants.DEVICE_TYPE, "android");
                                    final Disposable disposableAddDevice = clientApi.addDevice(header, body)
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(device -> {
                                                UserStore.getInstance(context).setDevice(device.getName(), device.getUuid());
                                                getConfig(context);
                                            }, throwable -> {
                                                setMutableLiveData(StatusResource.error(throwable.getMessage()));
                                            });
                                    compositeDisposable.add(disposableAddDevice);
                                } else {
                                    for (int i = (arrayListDevicesName.size() - 1); i >= arrayListDevicesName.size() - 1; i--) {
                                        if (arrayListDevicesName.get(i).contains(brandModel)) {
                                            final char[] arr = arrayListDevicesName.get(i).toCharArray();
                                            if (arr[arr.length - 2] != ')') {
                                                brandModel += "(2)" + SPACE + SEPARATOR + SPACE + imei;
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
                                                brandModel += "(" + count + ")" + SPACE + SEPARATOR + SPACE + imei;
                                            }
                                        }
                                        final HashMap<String, String> body = new HashMap<>();
                                        body.put(ApiConstants.DEVICE_NAME, brandModel);
                                        body.put(ApiConstants.DEVICE_TYPE, "android");
                                        Disposable disposableAddDevice = clientApi.addDevice(header, body)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(device -> {
                                                    UserStore.getInstance(context).setDevice(device.getName(), device.getUuid());
                                                    getConfig(context);
                                                }, throwable -> {
                                                    setMutableLiveData(StatusResource.error(throwable.getMessage()));
                                                });
                                        compositeDisposable.add(disposableAddDevice);
                                    }
                                }
                            }
                        }, throwable -> {
                            setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                        });
                compositeDisposable.add(disposableAllDevices);
            }

            private void getConfig(Context context) {
                final String deviceID = UserStore.getInstance(context).getDeviceID();
                final String token = UserStore.getInstance(context).getToken();
                Request request = new Request.Builder()
                        .url(BASE_URL + ApiConstants.CONFIG_DEVICE_URL + deviceID + ApiConstants.CONFIG_VPN_URL)
                        .addHeader(ApiConstants.AUTHORIZATION_HEADER, token)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(final okhttp3.Call call, final IOException e) {
                        setMutableLiveData(StatusResource.error(e.getMessage()));
                    }

                    @Override public void onResponse(final okhttp3.Call call, final Response response) throws IOException {
                        final InputStream inputStream = response.body().byteStream();
                        final Scanner scanner = new Scanner(inputStream).useDelimiter(DELIMITER);
                        final String data = scanner.hasNext() ? scanner.next() : "";
                        createTunnel(data);
                    }
                });
            }

            private void createTunnel(final String rawConfig) {
                try {
                    final byte[] configBytes = rawConfig.getBytes();
                    final Config config = Config.parse(new ByteArrayInputStream(configBytes));
                    Application.getTunnelManager().create(TUNNEL_NAME, config).whenComplete((observableTunnel, throwable) -> {
                        if (observableTunnel != null) {
                            TunnelStore.getInstance(context).setTunnel(TUNNEL_NAME, rawConfig);
                            tunnelManager.setTunnelState(observableTunnel,State.DOWN);
                            setMutableLiveData(StatusResource.success());
                        } else {
                            setMutableLiveData(StatusResource.error(throwable.getMessage()));
                        }
                    });
                } catch (Exception e) {
                    postMutableLiveData(StatusResource.error(e.getMessage()));
                }
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

    public void clearDisposable() {
        compositeDisposable.clear();
    }


    private Config parseConfig(String data) throws IOException, BadConfigException {
        final byte[] configText = data.getBytes();
        final Config config = Config.parse(new ByteArrayInputStream(configText));
        return config;
    }

    public TunnelManager getTunnelManager() {
        return tunnelManager;
    }

    public ObservableTunnel getTunnel(final Context context, final boolean stateTunnel){
        //TODO implement config is null case
        Config config = null;
        try {
            config = parseConfig(TunnelStore.getInstance(context).getConfig());
        } catch (final IOException | BadConfigException e) {
            return null;
        }
        final String name =  TunnelStore.getInstance(context).getTunnelName();
        final ObservableTunnel tunnel;
        if(stateTunnel){
            tunnel =  new ObservableTunnel(tunnelManager, name, config, State.UP);
        }
        else {
            tunnel =  new ObservableTunnel(tunnelManager, name, config, State.DOWN);
        }
        tunnelManager.setTunnelState(tunnel,tunnel.getState());

        return tunnel;
    }
    public void setUserURL(Context context, String url){
        UserStore.getInstance(context).setUserURL(url);
    }
}
