/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.api.network;

import com.wireguard.android.resource.StatusResource;

import androidx.annotation.MainThread;
import androidx.lifecycle.MutableLiveData;

public abstract class NetworkBoundStatusResource<T> {
    private final MutableLiveData<StatusResource<T>> mutableLiveData = new MutableLiveData<>();

    @MainThread
    public NetworkBoundStatusResource() {
        mutableLiveData.setValue(StatusResource.<T>loading());
        createCall();
    }

    @MainThread
    protected abstract void createCall();

    public void setMutableLiveData(StatusResource<T> value) {
        mutableLiveData.setValue(value);
    }

    public final MutableLiveData<StatusResource<T>> getMutableLiveData() {
        return mutableLiveData;
    }
}
