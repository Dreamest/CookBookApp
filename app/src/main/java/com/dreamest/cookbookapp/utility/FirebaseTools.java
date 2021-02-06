package com.dreamest.cookbookapp.utility;

import android.content.Context;
import android.graphics.Bitmap;
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

    public static void uploadImage(AppCompatActivity activity, StorageReference storageReference, ImageView imageView, boolean closeOnFinish) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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
}
