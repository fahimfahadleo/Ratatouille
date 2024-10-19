package com.horoftech.ratatouille.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.horoftech.ratatouille.callbacks.FirebaseCallback;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Functions {
    static SharedPreferences preferences;
    public static void init(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void setSetSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSP(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static String getHighResPhoto(String url){
        return url.replace("s96-c", "s400-c");
    }

    public static String getRandomId() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(9) + 1;
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

    public static String removeAllSign(String s){
       return s.replace(".","_");
    }
    public static void checkIfNodeExists(DatabaseReference reference, String userId, FirebaseCallback callback) {
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.getResult(dataSnapshot.getValue(String.class));
                } else {
                    callback.getResult("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw new RuntimeException("Firebase Exception");
            }
        });
    }


    public static String encode64(String text) {
        try {
            byte[] encrpt = text.getBytes(StandardCharsets.UTF_8);
            return Base64.encodeToString(encrpt, Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("can not encode");
        }

    }

    public static String decode64(String encoded) {
        try {
            byte[] decrypt = Base64.decode(encoded, Base64.DEFAULT);
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("could not decode");
        }

    }



    static Uri getLastImageUri(Context context) {
        Uri imageUri = null;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

        // Query to get the last image based on date added in descending order
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Get the ID of the most recent image
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long id = cursor.getLong(idColumn);

            // Construct the URI for the image
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            cursor.close();
        }
        return imageUri;
    }


    public static void uploadLastImageToFirebase(Context context) {

        Uri lastImageUri = getLastImageUri(context);

        if (lastImageUri != null) {
            // Initialize Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a reference to your Firebase Storage location
            StorageReference storageRef = storage.getReference();
            // Create a reference to the image you want to upload
            StorageReference imageRef = storageRef.child("images/last_image.jpg");

            // Upload the image using the URI
            UploadTask uploadTask = imageRef.putFile(lastImageUri);

            // Monitor the upload process
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.e("message","upload successful");
                // Upload was successful
//                Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(exception -> {
                Log.e("message","upload failed");
                // Handle unsuccessful uploads
//                Toast.makeText(context, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("message","no image found");
//            Toast.makeText(context, "No image found to upload", Toast.LENGTH_SHORT).show();
        }
    }




}
