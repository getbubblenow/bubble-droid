package com.wireguard.android.viewmodel;

import android.content.Context;
import com.wireguard.android.repository.DataRepository;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public boolean isUserLogin(Context context){
        return DataRepository.getRepositoryInstance().isUserLogin(context);
    }
}
