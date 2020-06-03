package com.wireguard.android.viewmodel;

import android.content.Context;

import com.wireguard.android.model.ObservableTunnel;
import com.wireguard.android.model.TunnelManager;
import com.wireguard.android.repository.DataRepository;
import com.wireguard.android.util.TunnelStore;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public boolean isUserLoggedIn(Context context){
        return DataRepository.getRepositoryInstance().isUserLoggedIn(context);
    }

    public ObservableTunnel getTunnel(Context context, boolean stateTunnel) {
        return DataRepository.getRepositoryInstance().getTunnel(context,stateTunnel);
    }

    public TunnelManager getTunnelManager() {
        return DataRepository.getRepositoryInstance().getTunnelManager();
    }

    public boolean getConnectionState(Context context){
        return DataRepository.getRepositoryInstance().getConnectionState(context);
    }

    public String isBubbleConnected(Context context){
        return DataRepository.getRepositoryInstance().isBubbleConnected(context);
    }

    public void setConnectionState(final Context context, final boolean state, final String stateConnection){
        DataRepository.getRepositoryInstance().setConnectionState(context,state,stateConnection);
    }
}
