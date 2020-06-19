package com.getbubblenow.android.viewmodel;

import android.content.Context;

import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public LiveData<StatusResource<byte[]>> login(String username, String password, Context context) {
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

    public void setHostName(Context context, String hostname){
        DataRepository.getRepositoryInstance().setHostName(context,hostname);
    }

    public MutableLiveData<StatusResource<Object>> createTunnel(Context context) {
        return DataRepository.getRepositoryInstance().createTunnel(context);
    }

    public MutableLiveData<StatusResource<byte[]>> getNodeLiveData() {
        return DataRepository.getRepositoryInstance().getNodeLiveData();
    }

    public void setNodeLiveData(final MutableLiveData<StatusResource<byte[]>> nodeLiveData) {
        DataRepository.getRepositoryInstance().setNodeLiveData(nodeLiveData);
    }

    public void login(Context context, String username, String password){
        DataRepository.getRepositoryInstance().login(context, username, password);
    }
}
