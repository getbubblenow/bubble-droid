package com.wireguard.android.viewmodel;

import android.content.Context;

import com.wireguard.android.model.ObservableTunnel;
import com.wireguard.android.repository.DataRepository;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public boolean isUserLoggedIn(Context context){
        return DataRepository.getRepositoryInstance().isUserLoggedIn(context);
    }

    public ObservableTunnel getTunnel(Context context) {
        return DataRepository.getRepositoryInstance().getTunnel(context);
    }
}
