package com.hkust.comp4521.hippos.rest;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by muppala on 4/4/15.
 */

public class RetrofitErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {

        if (cause.isNetworkError()) {
            if (cause.getMessage().contains("authentication")) {
                //401 errors
                return new Exception("Invalid credentials. Please verify login info.");
            } else if (cause.getCause() instanceof SocketTimeoutException) {
                //Socket Timeout
                return new SocketTimeoutException("Connection Timeout. " +
                        "Please verify your internet connection.");
            } else {
                //No Connection
                return new ConnectException("No Connection. " +
                        "Please verify your internet connection.");
            }
        } else {

            return cause;
        }
    }

}