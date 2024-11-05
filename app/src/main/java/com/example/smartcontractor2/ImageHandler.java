package com.example.smartcontractor2;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageHandler {

    private static final String TAG = "ImageHandler";
    private Context context;

    public ImageHandler(Context context) {
        this.context = context;
    }

    // Save image to album and return URI
    public Uri saveImageToAlbum(Bitmap bitmap) {
        OutputStream fos;
        String imageName = "SmartContractor_" + System.currentTimeMillis() + ".jpg";
        Uri imageUri = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SmartContractor");
                imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (imageUri != null) {
                    fos = context.getContentResolver().openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (fos != null) fos.close();
                    Toast.makeText(context, "Image saved to SmartContractor album", Toast.LENGTH_SHORT).show();
                }
            } else {
                File directory = new File(context.getExternalFilesDir(null), "SmartContractor");
                if (!directory.exists()) directory.mkdirs();
                File imageFile = new File(directory, imageName);
                fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(context, "Image saved to SmartContractor album", Toast.LENGTH_SHORT).show();
                imageUri = Uri.fromFile(imageFile);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to save image", e);
        }
        return imageUri;
    }

    public void uploadImage(Uri imageUri) {
        Log.d(TAG, "Uploading image from URI: " + imageUri.toString());
        // Implement upload logic here if needed
    }
}
