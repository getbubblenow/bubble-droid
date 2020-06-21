package com.getbubblenow.android.viewmodel;

import android.content.Context;

import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

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

    public void setHostName(Context context, String hostname){
        DataRepository.getRepositoryInstance().setHostName(context,hostname);
    }

    public MutableLiveData<StatusResource<Object>> createTunnel(Context context) {
        return DataRepository.getRepositoryInstance().createTunnel(context);
    }

    public MutableLiveData<StatusResource<byte[]>> login (Context context, String username, String password){
       return DataRepository.getRepositoryInstance().login(context, username, password);
    }
}
