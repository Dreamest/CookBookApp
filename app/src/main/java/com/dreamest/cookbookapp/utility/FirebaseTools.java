package com.dreamest.cookbookapp.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.activities.ProfileActivity;
import com.dreamest.cookbookapp.activities.WelcomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FirebaseTools {

    public static void downloadImage(Context context, String path, String fileName, String filePostfix, ImageView v, Drawable tempDrawableID, int onFailureDrawableID) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(path);
        try {
            File tempFile = File.createTempFile(fileName, filePostfix);
            ref.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Glide
                            .with(context)
                            .load(tempFile)
                            .centerCrop()
                            .into(v)
                            .onLoadStarted(tempDrawableID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    v.setImageResource(onFailureDrawableID);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadImage(AppCompatActivity activity, StorageReference storageReference, String path, boolean closeOnFinish) {
//        imageView.setDrawingCacheEnabled(true);
//        imageView.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(activity, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                Log.d("dddd", exception.getMessage());
                if(closeOnFinish) {
                    activity.finish();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(activity, R.string.upload_success, Toast.LENGTH_SHORT).show();
                if(closeOnFinish) {
                    activity.finish();
                }
            }
        });
    }


    /**
     * Creates a unique chat key based on two user IDs regardless if who calls this and adds the key to both users
     * @param uid1 id of user 1
     * @param uid2 id of user 2
     * @return chatKKey
     */
    public static String createChatKey(String uid1, String uid2) {
        String chatKey = uid1+uid2;
        if(uid2.compareTo(uid1) > 0) {
            chatKey = uid2+uid1;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS);

        // When this function is called we can already
        ref.child(uid1).child(UtilityPack.KEYS.MY_CHATS).child(chatKey).setValue(String.valueOf(System.currentTimeMillis()));
        ref.child(uid2).child(UtilityPack.KEYS.MY_CHATS).child(chatKey).setValue(String.valueOf(System.currentTimeMillis()));

        return chatKey;
    }
}
