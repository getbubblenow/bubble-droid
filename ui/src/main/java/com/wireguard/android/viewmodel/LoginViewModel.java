package com.wireguard.android.viewmodel;

import android.content.Context;

import com.wireguard.android.model.User;
import com.wireguard.android.repository.DataRepository;
import com.wireguard.android.resource.StatusResource;

import java.util.HashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public LiveData<StatusResource<User>> login(String username,String password, Context context){
        return DataRepository.getRepositoryInstance().login(username,password,context);
    }
}
