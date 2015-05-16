package com.hkust.comp4521.hippos.rest;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Yman on 15/5/2015.
 */
public interface ServerAPI {
    @FormUrlEncoded
    @POST("/login")
    void login(@Field("email") String email, @Field("password") String pw, Callback<Response_User> callback);

    @FormUrlEncoded
    @POST("/category")
    void createCategory(@Header("Authorization") String authorization, @Field("name") String category_name, Callback<Response_Category> callback);


    @GET("/category/")
    void getAllCategory(@Header("Authorization") String authorization, Callback<Response_CategoryList> callback);
}
