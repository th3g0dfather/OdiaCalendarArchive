package com.mohit.odiacalendararchive.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.mohit.odiacalendararchive.model.LoadImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CacheImageManager {
    public static File getImage(Context context, LoadImage loadImage) {
        String fileName = context.getFilesDir() + "/" + loadImage.getName();
        File file = new File(fileName);
        return file;
    }

    public static void putImage(Context context, LoadImage loadImage, Bitmap bitmap) {
       String fileName = context.getFilesDir()+"/"+loadImage.getName();

       File file = new File(fileName);

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
