package com.wireguard.android.api.network;

import com.wireguard.android.api.ApiConstants;
import com.wireguard.android.api.interceptor.AcceptInterceptor;
import com.wireguard.android.api.interceptor.UserAgentInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientService {
    private static volatile ClientService clientService = null;

    private ClientService() {

    }

    public static ClientService getInstance() {
        if (clientService == null) {
            synchronized (ClientService.class) {
                if (clientService == null) {
                    clientService = new ClientService();
                }
            }
        }
        return clientService;
    }


    public ClientApi createClientApi(String url) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new AcceptInterceptor());
        httpClient.addInterceptor(new UserAgentInterceptor(System.getProperty("http.agent")));

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build().create(ClientApi.class);
    }
}
