package com.hkust.comp4521.hippos.rest;

import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Category;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Yman on 15/5/2015.
 */
public class RestClient {
    public static final String SERVER_ID = "ec2-54-92-12-108.ap-northeast-1.compute.amazonaws.com/hippos/v1";
    public static final String SERVER_URL = "http://" + SERVER_ID;


    private static ServerAPI serverAPI;
    private String authorization;
    private Category myCategory;
    static {
        setupRestClient();
    }

    //public static ServerAPI get() {
    //return serverAPI;
    // }
    public RestClient() {
        authorization = "";
        myCategory = new Category();
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        serverAPI = restAdapter.create(ServerAPI.class);
    }

    public String login(String email, String password) {
        serverAPI.login(email, password, new Callback<Response_User>() {
            @Override
            public void success(Response_User responseUser, Response response) {
                if (!responseUser.error)
                    authorization = responseUser.apiKey;
                else authorization = "";
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return authorization;
    }

    public Category createCategory(final String category_name) {
        if (authorization.equals("")) return null;
        myCategory = new Category();
        myCategory.setName(category_name);

        serverAPI.createCategory(authorization, category_name, new Callback<Response_Category>() {
            @Override
            public void success(Response_Category response_category, Response response) {
                Log.i("RestAPI", "response_category error : " + response_category.error);
                if (!response_category.error) {
                    Log.i("RestAPI", "response_category id : " + response_category.getID());
                    myCategory.setID(response_category.getID());
                } else myCategory = null;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return myCategory;
    }

    // public List<Category>
}
