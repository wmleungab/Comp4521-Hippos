package com.hkust.comp4521.hippos.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.events.ConnectivitiyChangedEvent;

/**
 * Created by TC on 5/23/2015.
 */
public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //Retrieve connectivity information
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi.isAvailable() || mobile.isAvailable();

        if (isConnected) {
            // Explicitly specify that InvoiceHandler will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(), InvoiceHandler.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }

        Commons.getBusInstance().register(context);
        Commons.getBusInstance().post(new ConnectivitiyChangedEvent(isConnected));
        Commons.getBusInstance().unregister(context);
    }
}
