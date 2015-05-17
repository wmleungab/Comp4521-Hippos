package com.hkust.comp4521.hippos.rest;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

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

    @GET("/inventory/")
    void getAllInventory(@Header("Authorization") String authorization, Callback<Response_InventoryList> callback);

    @FormUrlEncoded
    @PUT("/inventory/{id}")
    void updateInventory(@Header("Authorization") String authorization, @Path("id") int id, @Field("name") String name
            , @Field("price") double price, @Field("stock") int stock, @Field("status") int status
            , @Field("category") int category, Callback<Response_Inventory> callback);

    @GET("/invoice/{id}")
    void getInvoice(@Header("Authorization") String authorization, @Path("id") int id, Callback<Response_Invoice> callback);

    @GET("/invoice/")
    void getAllInvoice(@Header("Authorization") String authorization, Callback<Response_InvoiceList> callback);

    @GET("/invoice/month/")
    void getMonthlyInvoice(@Header("Authorization") String authorization, Callback<Response_InvoiceList> callback);

    @GET("/invoice/day/")
    void getDailyInvoice(@Header("Authorization") String authorization, Callback<Response_InvoiceList> callback);

    @FormUrlEncoded
    @POST("/invoice")
    void createInvoice(@Header("Authorization") String authorization, @Field("total_price") double total_price, @Field("final_price") double final_price
            , @Field("content") String content, @Field("email") String email, Callback<Response_Invoice> callback);

    @FormUrlEncoded
    @PUT("/invoice/{id}")
    void updateInvoice(@Header("Authorization") String authorization, @Path("id") int id, @Field("total_price") double total_price, @Field("final_price") double final_price
            , @Field("content") String content, @Field("email") String email, @Field("status") int status, Callback<Response_Invoice> callback);

    @FormUrlEncoded
    @POST("/upload")
    void uploadFile(@Header("Authorization") String authorization, @Field("name") String fileName, @Field("extension") String fileExten, @Field("content") String fileContent, Callback<Response_FileUpload> callback);


    @Multipart
    @POST("/upload/{id}")
    void uploadImage(@Header("Authorization") String authorization, @Path("id") int inven_id, @Part("file") TypedFile file, Callback<Response_FileUpload> callback);
}
