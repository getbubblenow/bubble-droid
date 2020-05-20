package com.wireguard.android.repository;

import com.wireguard.android.api.network.ClientApi;
import com.wireguard.android.api.network.ClientService;

public class DataRepository {
    private static volatile DataRepository instance;
    private ClientApi clientApi;

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

}
