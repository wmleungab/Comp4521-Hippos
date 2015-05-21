package com.hkust.comp4521.hippos.rest;

/**
 * Created by Yman on 16/5/2015.
 */


public interface RestListener<T> {
    public static final int AUTHORIZATION_FAIL = 1;
    public static final int INVALID_PARA = 2;
    public static final int NOT_EXIST_OR_SAME_VALUE = 3;
    public static final int INVALID_EMAIL = 4;
    public static final int UPLOAD_FAIL = 5;
    public static final int DOWNLOAD_FAIL = 6;
    public static final int HIPPOS_SERVER_ERROR = 500;
    public static final int UNKNOWN_REASON = 999;
    public static final int NETWORK_UNREACHABLE = 900;

    public void onSuccess(T t);

    /*
    status:1 authorization problem
            2 missing argument
            3 required resource doesn't exist
                having the same value=> cannot update
     */
    public void onFailure(int status);

}
