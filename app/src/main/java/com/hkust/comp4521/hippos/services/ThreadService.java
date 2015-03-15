package com.hkust.comp4521.hippos.services;

import android.app.Activity;
import android.graphics.Point;

/**
 * Created by TC on 3/15/2015.
 */
public class ThreadService {

    public static void delayedStart(final Activity mact, final Runnable runnable, final int delaymSec) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delaymSec);
                    mact.runOnUiThread(runnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
