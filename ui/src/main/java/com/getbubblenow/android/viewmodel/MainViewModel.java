package com.getbubblenow.android.viewmodel;

import android.content.Context;

import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.model.ObservableTunnel;
import com.getbubblenow.android.model.TunnelManager;
import com.getbubblenow.android.repository.DataRepository;
import com.getbubblenow.android.util.TunnelStore;
import com.getbubblenow.android.util.UserStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public boolean isUserLoggedIn(Context context){
        return !UserStore.USER_TOKEN_DEFAULT_VALUE.equals(UserStore.getInstance(context).getToken());
    }

    public ObservableTunnel getTunnel(Context context, boolean stateTunnel) {
        return DataRepository.getRepositoryInstance().getTunnel(context,stateTunnel);
    }

    public void buildRepositoryInstance(Context context, String url){
        DataRepository.buildRepositoryInstance(context,url);
    }

    public String getUserURL(Context context){
        return UserStore.getInstance(context).getUserURL();
    }

    public MutableLiveData<Boolean> connectWithPermission(final boolean checked , Context context) {
        return DataRepository.getRepositoryInstance().connectWithPermission(checked,context);
    }

    public MutableLiveData<Boolean> connect(final Boolean checked , Context context) {
        return DataRepository.getRepositoryInstance().connect(checked,context);
    }

    public boolean isVPNConnected(Context context, boolean connectionStateFlag) {
        return DataRepository.getRepositoryInstance().isVPNConnected(context,connectionStateFlag);
    }

    public String getHostname(Context context){
        return DataRepository.getRepositoryInstance().getHostname(context);
    }

    public void removeSharedPreferences(Context context){
        DataRepository.getRepositoryInstance().removeSharedPreferences(context);
    }

    public void deleteTunnel(Context context){
        DataRepository.getRepositoryInstance().deleteTunnel(context);
    }
}
