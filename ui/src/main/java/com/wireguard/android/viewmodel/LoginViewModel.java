package com.wireguard.android.viewmodel;

import android.content.Context;

import com.wireguard.android.model.User;
import com.wireguard.android.repository.DataRepository;
import com.wireguard.android.resource.StatusResource;
import com.wireguard.android.util.UserStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public LiveData<StatusResource<User>> login(String username, String password, Context context) {
        return DataRepository.getRepositoryInstance().login(username, password, context);
    }

    @Override protected void onCleared() {
        super.onCleared();
        DataRepository.getRepositoryInstance().clearDisposable();
    }

    public void buildRepositoryInstance(Context context, String url){
        DataRepository.buildRepositoryInstance(context,url);
    }

    public void setUserURL(Context context, String url){
        DataRepository.getRepositoryInstance().setUserURL(context,url);
    }

    public void buildClientService(String url){
        DataRepository.getRepositoryInstance().buildClientService(url);
    }

    public MutableLiveData<byte[]> getCertificate(Context context){
        return DataRepository.getRepositoryInstance().getCertificate(context);
    }

    public void setHostName(Context context, String hostname){
        DataRepository.getRepositoryInstance().setHostName(context,hostname);
    }
}
