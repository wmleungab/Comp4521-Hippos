package com.hkust.comp4521.hippos.services;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by TC on 3/15/2015.
 */
public class TintedStatusBar {

    public static void changeStatusBarColor(Activity mActivity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int darkerColor = getDarkerColor(color, 0.75f);
            window.setStatusBarColor(darkerColor);
        }
    }

    public static int getColorFromTag(View view) {
        if(view.getTag() == null)
            return 0;
        return Color.parseColor((String) view.getTag());
    }

    public static int getDarkerColor (int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
}
