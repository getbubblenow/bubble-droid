package com.getbubblenow.android.repository;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.getbubblenow.android.Application;
import com.getbubblenow.android.R;
import com.getbubblenow.android.configStore.FileConfigStore;
import com.getbubblenow.android.model.ObservableTunnel;
import com.getbubblenow.android.model.TunnelManager;
import com.getbubblenow.android.activity.MainActivity;
import com.getbubblenow.android.api.ApiConstants;
import com.getbubblenow.android.api.network.ClientApi;
import com.getbubblenow.android.api.network.ClientService;
import com.getbubblenow.android.api.network.NetworkBoundStatusResource;
import com.getbubblenow.android.util.UtilKt;
import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Tunnel;
import com.wireguard.android.backend.Tunnel.State;
import com.getbubblenow.android.model.Device;
import com.getbubblenow.android.resource.StatusResource;
import com.getbubblenow.android.util.TunnelStore;
import com.getbubblenow.android.util.UserStore;
import com.wireguard.config.BadConfigException;
import com.wireguard.config.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.HttpException;

public class DataRepository {
    private static volatile DataRepository instance;
    private ClientApi clientApi;
    private CompositeDisposable compositeDisposable;
    private final OkHttpClient client = new OkHttpClient();
    private ObservableTunnel pendingTunnel;

    private static final String SEPARATOR = ":";
    private static final String SPACE = " ";
    private static final String DELIMITER = "\\A";
    private static final int ANDROID_ID = 1;
    private static String BASE_URL = "";
    private static final String TUNNEL_NAME = "Bubble";
    private static final int REQUEST_CODE_VPN_PERMISSION = 23491;
    private static final String NO_INTERNET_CONNECTION = "no internet connection";
    private static final String LOGIN_FAILED = "Login Failed";
    private static String token = "";
    private static String deviceName;
    private static String deviceID;

    private DataRepository(Context context, String url) {
        BASE_URL = url;
        clientApi = ClientService.getInstance().createClientApi(url);
        compositeDisposable = new CompositeDisposable();
        Application.setTunnelManager(new TunnelManager(new FileConfigStore(context)));
    }

    public static void buildRepositoryInstance(Context context, String url) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(context, url);
                }
            }
        }
    }

    public void buildClientService(String url) {
        BASE_URL = url;
        clientApi = ClientService.getInstance().createClientApi(url);
    }

    public static DataRepository getRepositoryInstance() {
        return instance;
    }

    public MutableLiveData<StatusResource<byte[]>> login(String username, String password, Context context) {
        return new NetworkBoundStatusResource<byte[]>() {
            @Override protected void createCall() {
                HashMap<String, String> data = new HashMap<>();
                data.put(ApiConstants.USERNAME, username);
                data.put(ApiConstants.PASSWORD, password);
                Disposable disposableLogin = clientApi.login(data)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            token = user.getToken();
                            if (!isDeviceLoggedIn(context)) {
                                addDevice(context);
                            } else {
                                getAllDevices(context);
                            }
                        }, throwable -> {
                            setErrorMessage(throwable,this,null);
                        });
                compositeDisposable.add(disposableLogin);
            }

            private void getAllDevices(final Context context) {
                final HashMap<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTHORIZATION_HEADER, token);
                Disposable disposableAllDevices = clientApi.getAllDevices(header)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listDevices -> {
                            boolean hasDevice = false;
                            for (Device item : listDevices) {
                                if (UserStore.getInstance(context).getDeviceID().equals(item.getUuid())) {
//                                    UserStore.getInstance(context).setToken(token);
                                    setMutableLiveData(StatusResource.success(null));
                                    hasDevice = true;
                                    break;
                                }
                            }
                            if (!hasDevice) {
                                addDevice(context);
                            }
                        }, throwable -> {
                            setErrorMessage(throwable,this,null);
                        });
                compositeDisposable.add(disposableAllDevices);
            }

            private void addDevice(final Context context) {
                final String brand = getBrand();
                final String model = getDeviceModel();
                final String imei = getDeviceID(context);
                final String deviceName = brand + SPACE + model + SPACE + SEPARATOR + SPACE + imei;
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
                                        DataRepository.deviceName = device.getName();
                                        DataRepository.deviceID = device.getUuid();
                                        hasDevice = true;
                                        getCertificate(context).observe((LifecycleOwner) context, new Observer<StatusResource<byte[]>>() {
                                            @Override public void onChanged(final StatusResource<byte[]> statusResource) {
                                                switch (statusResource.status){
                                                    case SUCCESS:
                                                        postMutableLiveData(StatusResource.success(statusResource.data));
                                                        break;
                                                    case ERROR:
                                                        postMutableLiveData(StatusResource.error(statusResource.message));
                                                        break;
                                                }
                                            }
                                        });
//                                        getConfig(context);
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
                                                getCertificate(context).observe((LifecycleOwner) context, new Observer<StatusResource<byte[]>>() {
                                                    @Override public void onChanged(final StatusResource<byte[]> statusResource) {
                                                        switch (statusResource.status){
                                                            case SUCCESS:
                                                                postMutableLiveData(StatusResource.success(statusResource.data));
                                                                break;
                                                            case ERROR:
                                                                postMutableLiveData(StatusResource.error(statusResource.message));
                                                                break;
                                                        }
                                                    }
                                                });
//                                                getConfig(context);
                                            }, throwable -> {
                                                setErrorMessage(throwable,this,null);
                                               // setMutableLiveData(StatusResource.error(throwable.getMessage()));
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
                                                    DataRepository.deviceName = device.getName();
                                                    DataRepository.deviceID = device.getUuid();
                                                //    UserStore.getInstance(context).setDevice(device.getName(), device.getUuid());
                                                    getCertificate(context).observe((LifecycleOwner) context, new Observer<StatusResource<byte[]>>() {
                                                        @Override public void onChanged(final StatusResource<byte[]> statusResource) {
                                                            switch (statusResource.status){
                                                                case SUCCESS:
                                                                    postMutableLiveData(StatusResource.success(statusResource.data));
                                                                    break;
                                                                case ERROR:
                                                                    postMutableLiveData(StatusResource.error(statusResource.message));
                                                                    break;
                                                            }
                                                        }
                                                    });
//                                                    getConfig(context);
                                                }, throwable -> {
                                                    setErrorMessage(throwable,this,null);
                                                   // setMutableLiveData(StatusResource.error(throwable.getMessage()));
                                                });
                                        compositeDisposable.add(disposableAddDevice);
                                    }
                                }
                            }
                        }, throwable -> {
                            setErrorMessage(throwable,this,null);
                         //   setMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
                        });
                compositeDisposable.add(disposableAllDevices);
            }
        }.getMutableLiveData();
    }

    public MutableLiveData<StatusResource<Object>> createTunnel(Context context) {
       return new NetworkBoundStatusResource<Object>(){

           @Override protected void createCall() {
               NetworkBoundStatusResource<Object> liveData = this;
               Request request = new Request.Builder()
                       .url(BASE_URL + ApiConstants.CONFIG_DEVICE_URL + deviceID + ApiConstants.CONFIG_VPN_URL)
                       .addHeader(ApiConstants.AUTHORIZATION_HEADER, token)
                       .build();

               client.newCall(request).enqueue(new Callback() {
                   @Override public void onFailure(final okhttp3.Call call, final IOException e) {
                       setErrorMessage(e,liveData,null);
                   }

                   @Override public void onResponse(final okhttp3.Call call, final Response response) throws IOException {
                       if(response.isSuccessful()) {
                           final InputStream inputStream = response.body().byteStream();
                           final Scanner scanner = new Scanner(inputStream).useDelimiter(DELIMITER);
                           final String data = scanner.hasNext() ? scanner.next() : "";

                           try {
                               final byte[] configBytes = data.getBytes();
                               final Config config = Config.parse(new ByteArrayInputStream(configBytes));
                               Application.getTunnelManager().create(TUNNEL_NAME, config).whenComplete((observableTunnel, throwable) -> {
                                   if (observableTunnel != null) {
                                       TunnelStore.getInstance(context).setTunnel(TUNNEL_NAME, data);
                                       Application.getTunnelManager().setTunnelState(observableTunnel, State.DOWN);
                                       UserStore.getInstance(context).setToken(token);
                                       UserStore.getInstance(context).setDevice(deviceName, deviceID);
                                       postMutableLiveData(StatusResource.success(null));
                                   } else {
                                       setErrorMessage(throwable, liveData, response);
                                   }
                               });
                           } catch (Exception e) {
                               setErrorMessage(e, liveData, response);
                           }
                       }
                       else {
                           setErrorMessage(null, liveData, response);
                       }
                   }
               });
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
        if(compositeDisposable!=null) {
            compositeDisposable.clear();
        }
    }


    private Config parseConfig(String data) throws IOException, BadConfigException {
        final byte[] configText = data.getBytes();
        final Config config = Config.parse(new ByteArrayInputStream(configText));
        return config;
    }


    private ObservableTunnel createTunnel(final Context context, final boolean stateTunnel) {
        //TODO implement config is null case
        Config config = null;
        try {
            config = parseConfig(TunnelStore.getInstance(context).getConfig());
        } catch (final IOException | BadConfigException e) {
            return null;
        }
        final String name = TunnelStore.getInstance(context).getTunnelName();
        final ObservableTunnel tunnel;
        if (stateTunnel) {
            tunnel = new ObservableTunnel(Application.getTunnelManager(), name, config, State.UP);
        } else {
            tunnel = new ObservableTunnel(Application.getTunnelManager(), name, config, State.DOWN);
        }
//        Application.getTunnelManager().setTunnelState(tunnel, tunnel.getState());
        pendingTunnel = tunnel;
        return tunnel;
    }

    public void setUserURL(Context context, String url) {
        UserStore.getInstance(context).setUserURL(url);
    }

    public ObservableTunnel getTunnel(Context context, boolean connectionStateFlag) {
        ObservableTunnel tunnel = Application.getTunnelManager().getLastUsedTunnel();
        if (tunnel == null) {
            tunnel = createTunnel(context, connectionStateFlag);
        }
        pendingTunnel = tunnel;
        return tunnel;
    }

    public MutableLiveData<StatusResource<byte[]>> getCertificate(Context context) {
       return new NetworkBoundStatusResource<byte[]>(){

           @Override protected void createCall() {
               final NetworkBoundStatusResource<byte[]> liveData = this;
               final Request request = new Request.Builder()
                       .url(BASE_URL + ApiConstants.CERTIFICATE_URL)
                       .build();
               client.newCall(request).enqueue(new Callback() {
                   @Override public void onFailure(final okhttp3.Call call, final IOException e) {
                       setErrorMessage(e, liveData,null);
                   }

                   @Override public void onResponse(final okhttp3.Call call, final Response response) throws IOException {
                       if (response.isSuccessful()) {
                           final InputStream inputStream = response.body().byteStream();
                           final Scanner scanner = new Scanner(inputStream).useDelimiter(DELIMITER);
                           final String data = scanner.hasNext() ? scanner.next() : "";
                           final byte[] cert = data.getBytes();
                           X509Certificate x509Certificate = null;
                           try {
                               x509Certificate = X509Certificate.getInstance(cert);
                           } catch (final CertificateException e) {
                               setErrorMessage(e, liveData,response);
                           }
                           try {
                               if (x509Certificate != null) {
                                   liveData.postMutableLiveData(StatusResource.success(x509Certificate.getEncoded()));
                               }
                           } catch (final CertificateEncodingException e) {
                               setErrorMessage(e, liveData,response);
                           }
                       } else {
                           setErrorMessage(null, liveData,response);
                       }
                   }
               });
           }
       }.getMutableLiveData();
    }

    public boolean isVPNConnected(Context context, boolean connectionStateFlag) {
        pendingTunnel = getTunnel(context, connectionStateFlag);
        return pendingTunnel.getState() == State.DOWN;
    }

    public MutableLiveData<Boolean> connect(final Boolean checked , Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        if (pendingTunnel != null) {
            Application.getBackendAsync().thenAccept(backend -> {
                if (backend instanceof GoBackend) {
                    final Intent intent = GoBackend.VpnService.prepare(context);
                    if (intent != null) {
                        if(context instanceof MainActivity) {
                            ((MainActivity)context).startActivityForResult(intent, REQUEST_CODE_VPN_PERMISSION);
                            return;
                        }
                    }
                }
                connectWithPermission(checked,context).observe((LifecycleOwner) context, new Observer<Boolean>() {
                    @Override public void onChanged(final Boolean aBoolean) {
                        liveData.postValue(aBoolean);
                    }
                });
            });
        }
        return liveData;
    }

    public MutableLiveData<Boolean> connectWithPermission(final boolean checked , Context context) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        pendingTunnel.setStateAsync(Tunnel.State.of(checked)).whenComplete((observableTunnel, throwable) ->{
            if(throwable==null){
                if(observableTunnel == State.DOWN) {
                    liveData.postValue(false);
                }
                else  {
                    liveData.postValue(true);
                }
            }
            else {
                Toast.makeText(context,context.getString(R.string.failed_bubble),Toast.LENGTH_SHORT).show();
            }
        });
        return liveData;
    }


    public void setHostName(Context context, String hostname){
        UserStore.getInstance(context).setHostname(hostname);
    }

    public String getHostname(Context context){
        return UserStore.getInstance(context).getHostname();
    }

    private <T> void setErrorMessage(Throwable throwable , NetworkBoundStatusResource<T> liveData , Response response){
        if(response==null) {
            if (throwable instanceof IOException) {
                liveData.postMutableLiveData(StatusResource.error(NO_INTERNET_CONNECTION));
            }
            if (throwable instanceof HttpException) {
                if (((HttpException) throwable).code() == 500) {
                    final String requestURL = ((HttpException) throwable).response().raw().request().url().toString();
                    final String requestMethod = ((HttpException) throwable).response().raw().request().method();
                    final String requestBody = bodyToString(((HttpException) throwable).response().raw().request());
                    final String stackTrace = Arrays.toString(throwable.getStackTrace());
                    final String message = "URL:" + requestURL + '\n' +
                            "BODY:" + requestBody + '\n' +
                            "METHOD:" + requestMethod + '\n' +
                            "STACK_TRACE:" + stackTrace;
                    liveData.postMutableLiveData(StatusResource.error(message));
                } else {
                    liveData.postMutableLiveData(StatusResource.error(LOGIN_FAILED));
                }
            }
        }
        else {
            if (response.code() == 500) {
                final String requestURL = response.request().url().toString();
                final String requestMethod = response.request().method();
                final String requestBody = bodyToString(response.request());
                //TODO get stackTrace
                final String stackTrace = "";
                final String message = "URL:" + requestURL + '\n' +
                        "BODY:" + requestBody + '\n' +
                        "METHOD:" + requestMethod + '\n' +
                        "STACK_TRACE:" + stackTrace;
                liveData.postMutableLiveData(StatusResource.error(message));
            } else {
                liveData.postMutableLiveData(StatusResource.error(LOGIN_FAILED));
            }
        }
    }

    private String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public void removeSharedPreferences(Context context){
        context.getSharedPreferences(UserStore.USER_SHARED_PREF,0).edit().clear().apply();
        context.getSharedPreferences(TunnelStore.TUNNEL_SHARED_PREF,0).edit().clear().apply();
    }

    public void deleteTunnel(Context context){
        ArrayList<ObservableTunnel> tunnels = new ArrayList<>();
        tunnels.add(pendingTunnel);
//        UtilKt.deleteTunnel(tunnels);
        Application.getTunnelManager().delete(pendingTunnel);
//        ArrayList<ObservableTunnel> tunnels = new ArrayList<>();
//        tunnels.add(tunnelManager.getLastUsedTunnel());
//        UtilKt.deleteTunnel(tunnels);
    }
}
