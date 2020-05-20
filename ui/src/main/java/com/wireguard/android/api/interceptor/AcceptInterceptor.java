/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.wireguard.android.api.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AcceptInterceptor implements Interceptor {
    private final String ACCEPT_KEY = "Content-Type";
    private final String ACCEPT_HEADER = "application/json";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().header(ACCEPT_KEY, ACCEPT_HEADER);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
