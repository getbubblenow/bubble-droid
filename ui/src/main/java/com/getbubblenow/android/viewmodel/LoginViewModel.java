package com.getbubblenow.android.viewmodel;

import android.content.Context;

import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;
import com.getbubblenow.android.model.User;
import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.resource.StatusResource;
import com.getbubblenow.android.util.UserStore;

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

    public MutableLiveData<byte[]> getCertificate(Context context){
        return DataRepository.getRepositoryInstance().getCertificate(context);
    }

    public void setHostName(Context context, String hostname){
        DataRepository.getRepositoryInstance().setHostName(context,hostname);
    }

    public MutableLiveData<StatusResource<Object>> getConfig(Context context) {
        return DataRepository.getRepositoryInstance().getConfig(context);
    }
}
