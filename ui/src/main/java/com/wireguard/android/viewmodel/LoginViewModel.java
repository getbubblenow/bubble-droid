package com.wireguard.android.viewmodel;

import android.content.Context;

import com.wireguard.android.model.User;
import com.wireguard.android.repository.DataRepository;
import com.wireguard.android.resource.StatusResource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public LiveData<StatusResource<User>> login(String tunnelName, String username, String password, Context context) {
        return DataRepository.getRepositoryInstance().login(tunnelName,username, password, context);
    }

    @Override protected void onCleared() {
        super.onCleared();
        DataRepository.getRepositoryInstance().clearDisposable();
    }
}
