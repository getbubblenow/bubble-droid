package com.wireguard.android.repository;

import com.wireguard.android.api.network.ClientApi;
import com.wireguard.android.api.network.ClientService;
import com.wireguard.android.api.network.NetworkBoundStatusResource;
import com.wireguard.android.model.User;
import com.wireguard.android.resource.StatusResource;
import java.util.HashMap;
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

    public MutableLiveData<StatusResource<User>> login(final HashMap<String, String> params){
        return new NetworkBoundStatusResource<User>(){

            @Override protected void createCall() {
                clientApi.login(params).enqueue(new Callback<User>() {
                    @Override public void onResponse(final Call<User> call, final Response<User> response) {
                        if(response.isSuccessful()) {
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


    private String createErrorMessage(Call call, retrofit2.Response response) {
        return "Error: User agent: " + System.getProperty("http.agent") + ", Request body: " + call.request().body() + ", URL: " +
                call.request().url() + ", Code: " + response.code() + ", Message: " +
                response.message();
    }

}
