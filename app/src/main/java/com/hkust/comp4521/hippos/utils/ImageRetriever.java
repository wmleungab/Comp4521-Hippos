package com.hkust.comp4521.hippos.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.hkust.comp4521.hippos.rest.RestClient;
import com.hkust.comp4521.hippos.services.TintedStatusBar;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by TC on 5/19/2015.
 */
/*
Image AsyncTask: for loading image from either local storage or fetch remotely (if needed)
 */
public class ImageRetriever extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Drawable defaultDrawable = null;
    private final String fileUrl;
    private Boolean fileExist = false;
    private Activity activity = null;        // for tinted status bar
    private int sbColor;

    // For trivial use
    public ImageRetriever(ImageView imageView, String fileName, Drawable defaultDrawable) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        fileUrl = fileName;
        this.defaultDrawable = defaultDrawable;
    }

    // For tinted status bar
    public ImageRetriever(ImageView imageView, String fileName, Drawable defaultDrawable, Activity mActivity) {
        this(imageView, fileName, defaultDrawable);
        activity = mActivity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // skip if no url supplied
        if(fileUrl != null && fileUrl.equals(""))
            return null;
        // Check if file exists locally first
        Bitmap bitmap = null;
        File file = new File(ImageUtils.IMAGE_CACHE_PATH + fileUrl);
        Log.i("ImageRetriever", "Loading: " + file.getAbsolutePath());
        fileExist = file.exists();
        if(fileExist) {
            // load locally
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            // download from remote server
            try {
                bitmap = RestClient.getInstance().downloadAsBitmap(fileUrl);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        // generate color for status bar if needed
        if(activity != null && bitmap != null) {
            sbColor = Palette.generate(bitmap).getDarkVibrantColor(Color.parseColor("#696969"));
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        // Change image view if bitmap exists
        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    // cache to local storage
                    if(!fileExist)
                        ImageUtils.writeBitmapToFile(bitmap, ImageUtils.IMAGE_CACHE_PATH + fileUrl);
                    // set as view
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(defaultDrawable);
                }
                // animate the view
                Animation am = new AlphaAnimation( 0, 1 );
                am.setDuration(300);
                imageView.startAnimation(am);
            }
        }

        // Change status bar color if needed
        if(activity != null) {
            // Change action bar theme
            TintedStatusBar.changeStatusBarColor(activity, sbColor);
        }
    }
}
