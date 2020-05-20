/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.resource;

import com.wireguard.android.api.enums.Status;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatusResource<T> {
    @NonNull
    public final Status status;

    @Nullable
    public final String message;


    private StatusResource(@NonNull Status status, @Nullable String message) {
        this.status = status;
        this.message = message;
    }


    public static <T> StatusResource<T> success() {
        return new StatusResource<>(Status.SUCCESS,null);
    }

    public static <T> StatusResource<T> error(String msg) {
        return new StatusResource<>(Status.ERROR,msg);
    }

    public static <T> StatusResource<T> loading() {
        return new StatusResource<>(Status.LOADING,null);
    }
}
