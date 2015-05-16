package com.hkust.comp4521.hippos.rest;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

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

    @GET("/category/{id}")
    void getCategory(@Header("Authorization") String authorization, @Path("id") int id, Callback<Response_Category> callback);

    @FormUrlEncoded
    @PUT("/category/{id}")
    void updateCategory(@Header("Authorization") String authorization, @Path("id") int id, @Field("name") String name, Callback<Response_Category> callback);

    @FormUrlEncoded
    @POST("/inventory")
    void createInventory(@Header("Authorization") String authorization, @Field("name") String name, @Field("price") double price
            , @Field("stock") int stock, @Field("category") int category, Callback<Response_Inventory> callback);

    @GET("/inventory/{id}")
    void getInventory(@Header("Authorization") String authorization, @Path("id") int id, Callback<Response_Inventory> callback);
}
