package com.hkust.comp4521.hippos.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

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
    private final String fileUrl;
    private Activity activity = null;        // for tinted status bar
    private int sbColor;

    public ImageRetriever(ImageView imageView, String fileName) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        fileUrl = fileName;
    }

    public ImageRetriever(ImageView imageView, String fileName, Activity mActivity) {
        this(imageView, fileName);
        activity = mActivity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // Check if file exists locally first
        Bitmap bitmap = null;
        File file = new File(ImageUtils.IMAGE_CACHE_PATH + fileUrl);
        if(file.exists()) {
            // load locally
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            // download from remote server

        }
        // generate color for status bar if needed
        if(activity != null && bitmap != null) {
            sbColor = Palette.generate(bitmap).getDarkVibrantColor(Color.parseColor("#696969"));
        }
    /*
        RestClient.getInstance().downloadFile(fileUrl, new RestListener<InputStream>() {
            @Override
            public void onSuccess(InputStream inputStream) {
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                }
            }

            @Override
            public void onFailure(int status) {
            }
        });*/
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
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        // Change status bar color if needed
        if(activity != null) {
            // Change action bar theme
            TintedStatusBar.changeStatusBarColor(activity, sbColor);
        }
    }
}
