package com.hkust.comp4521.hippos.utils;

import android.graphics.Bitmap;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for image related methods.
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

    public static Bitmap getSizedBitmap(Bitmap inputBitmap) throws IOException {
        int width = inputBitmap.getWidth();
        int height = inputBitmap.getHeight();
        float ratio = (float)width / height;
        // Limit resolution to have height max=512 pixel
        height = 512;
        width = (int) (512 * ratio);
        // Rotate bitmap by 90 deg
        //Matrix matrix = new Matrix();
        //matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true);
        //Bitmap outputBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        Bitmap outputBitmap = scaledBitmap;

        return outputBitmap;
    }

    public static File writeBitmapToFile(Bitmap inputBM, String filePath) {
        FileOutputStream out = null;
        File file = null;
        try {
            out = new FileOutputStream(filePath);
            inputBM.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
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

    public static void deleteFile(Inventory inv) {
        // Delete cache file
        File file = new File(IMAGE_CACHE_PATH + inv.getImage());
        if(file.exists()) {
            file.delete();
        }
        // Mark info as dirty
        inv.setStatus(Inventory.INVENTORY_DIRTY);
    }

    public static void clearImageCache() {
        File dir = new File(IMAGE_CACHE_PATH);
        if(dir.exists()) {
            for(File file: dir.listFiles())
                file.delete();
        }
    }
}
