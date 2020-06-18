package com.getbubblenow.android.resource;

import com.getbubblenow.android.api.enums.Status;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StatusResource<T> {
    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    @Nullable
    public final T data;


    private StatusResource(@NonNull Status status, @Nullable String message, @Nullable T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public static <T> StatusResource<T> success(T data) {
        return new StatusResource<>(Status.SUCCESS,null,data);
    }

    public static <T> StatusResource<T> error(String msg) {
        return new StatusResource<>(Status.ERROR,msg,null);
    }

    public static <T> StatusResource<T> loading() {
        return new StatusResource<>(Status.LOADING,null,null);
    }
}
