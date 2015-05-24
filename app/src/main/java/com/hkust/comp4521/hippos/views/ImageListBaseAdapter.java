package com.hkust.comp4521.hippos.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.hkust.comp4521.hippos.R;
import com.hkust.comp4521.hippos.utils.ImageRetriever;

import java.lang.ref.WeakReference;

/**
 * Created by TC on 5/24/2015.
 */
// As suggested in:
// http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
public abstract class ImageListBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    protected LruCache<String, Bitmap> mMemoryCache;

    public ImageListBaseAdapter() {
        // Get memory class of this device, exceeding this amount will throw an OutOfMemory exception.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number of items.
                return bitmap.getByteCount();
            }
        };
    }

    public void setBitmapToView(Context mContext, int bitmapId, String bitmapName, ImageView toImageView, int targetSize) {
        Bitmap cacheBM = getBitmapFromMemCache(bitmapName);
        if(cacheBM != null) {
            // bitmap exists on cache, load directly from cache
            Log.i("onInventoryInfoChanged", "Loaded cache image for " + bitmapName);
            toImageView.setImageBitmap(cacheBM);
        } else {
            // bitmap does not exist on cache, load image from asynctask (that is cancellable)
            if (cancelPotentialWork(bitmapId, toImageView)) {
                Log.i("onInventoryInfoChanged", "Started Image Retriever for " + bitmapName);
                Bitmap bm = ((BitmapDrawable) mContext.getResources().getDrawable(R.mipmap.placeholder)).getBitmap();
                final ImageRetriever task = new ImageRetriever(toImageView, bitmapName, mContext.getResources().getDrawable(R.mipmap.placeholder), mMemoryCache, bitmapId, targetSize);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), bm, task);
                toImageView.setImageDrawable(asyncDrawable);
                task.execute();
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageRetriever> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ImageRetriever bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<ImageRetriever>(bitmapWorkerTask);
        }

        public ImageRetriever getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final ImageRetriever bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.invId;
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static ImageRetriever getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) mMemoryCache.get(key);
    }


}

