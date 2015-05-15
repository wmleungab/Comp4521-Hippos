package com.hkust.comp4521.hippos.rest;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Yman on 15/5/2015.
 */
public class RestClient {
    public static final String SERVER_ID = "ec2-54-92-12-108.ap-northeast-1.compute.amazonaws.com/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;


    private static ServerAPI restClient;

    static {
        setupRestClient();
    }

    public static ServerAPI get() {
        return restClient;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        restClient = restAdapter.create(ServerAPI.class);
    }
}
