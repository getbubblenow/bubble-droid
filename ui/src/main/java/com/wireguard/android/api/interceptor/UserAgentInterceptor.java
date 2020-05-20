package com.wireguard.android.api.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class UserAgentInterceptor implements Interceptor {
    private final String USER_AGENT_HEADER_KEY = "User-Agent";
    private String userAgent;

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().header(USER_AGENT_HEADER_KEY, userAgent);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
