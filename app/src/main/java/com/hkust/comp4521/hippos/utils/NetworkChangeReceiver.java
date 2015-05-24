package com.hkust.comp4521.hippos.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.events.ConnectivitiyChangedEvent;

/**
 * Created by TC on 5/23/2015.
 */
public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    public static boolean previouslyConnected = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // Get connectivity info
        if (intent == null || !ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            return;
        }
        boolean hasConnectivity = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        if (hasConnectivity) {
            Log.i("onReceive", "hasConnectivity");
            if(!previouslyConnected) {
                Log.i("onReceive", "Connected");
                // Explicitly specify that InvoiceHandler will handle the intent.
                ComponentName comp = new ComponentName(context.getPackageName(), InvoiceHandler.class.getName());
                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, (intent.setComponent(comp)));
                previouslyConnected = true;

                // Set online flag
                Commons.ONLINE_MODE = true;

                // Send bus event
                Commons.getBusInstance().register(context);
                Commons.getBusInstance().post(new ConnectivitiyChangedEvent(hasConnectivity));
                Commons.getBusInstance().unregister(context);
            }
        } else {
            previouslyConnected = false;
            Commons.ONLINE_MODE = false;
        }
    }
}
