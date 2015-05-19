package com.hkust.comp4521.hippos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.hkust.comp4521.hippos.datastructures.Commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for image related methods.
 * Method gathered from various sources
 * https://raw.githubusercontent.com/maiatoday/autoSelfie/master/AutoSelfie/src/main/java/za/co/maiatoday/autoselfie/util/ImageUtils.java
 * https://github.com/Trinea/android-common/blob/master/src/cn/trinea/android/common/util/ImageUtils.java
 */
public class ImageUtils {

    public static String IMAGE_CACHE_PATH = Commons.APP_ROOT_PATH + "cache" + File.separator;
    public static String UPLOAD_IMAGE_PATH = Commons.APP_ROOT_PATH + "to_upload.jpg";

    public static void checkAppFolderStructure() {
        File file = new File(Commons.APP_ROOT_PATH);
        if(!file.exists()) {
            file.mkdirs();
        }
        file = new File(IMAGE_CACHE_PATH);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getUniqueImageFilename(String extension) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String fileName = formatter.format(now) + extension;
        return fileName;
    }

    public static Bitmap getSizedBitmap(Context context, Uri uri, int size) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1)
            return null;

        int originalSize = onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = size == 0 ? 1.0 : originalSize > size ? originalSize / (double) size : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public static File writeBitmapToFile(Bitmap inputBM, String filePath) {
        FileOutputStream out = null;
        File file = null;
        try {
            out = new FileOutputStream(filePath);
            inputBM.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is your Bitmap instance
            file = new File(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
