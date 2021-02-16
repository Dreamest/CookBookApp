package com.dreamest.cookbookapp.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.logic.ChatMessage;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FirebaseTools {

    public static final int ADD = 1;
    public static final int REMOVE = 2;


    /**
     * Downloads image from FirebaseStorage and store it in given imageView as long as the activity isn't destroyed yet.
     *
     * @param context             activity context
     * @param path                path in Firebase Storage to the image
     * @param fileName            prefix for tempFile creation
     * @param filePostfix         postfix for tempFile creation
     * @param v                   imageView where the image will be downloaded to
     * @param tempDrawableID      drawable to display while downloading.
     * @param onFailureDrawableID drawable to display if failed downloading
     */
    public static void downloadImage(Context context, String path, String fileName, String filePostfix, ImageView v, Drawable tempDrawableID, int onFailureDrawableID) {
        if(path.trim().equals("")) { //if no image on database
            v.setImageResource(onFailureDrawableID);
            return;
        }
        StorageReference ref = FirebaseStorage.getInstance().getReference(path);
        try {
            File tempFile = File.createTempFile(fileName, filePostfix);
            ref.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (!((AppCompatActivity) context).isDestroyed()) {
                        Glide
                                .with(context)
                                .load(tempFile)
                                .centerCrop()
                                .into(v)
                                .onLoadStarted(tempDrawableID);
                    }


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

    /**
     * Uploads image from file to FirebaseStorage
     *
     * @param activity         activity context
     * @param storageReference where the file will be stored
     * @param path             path to image file
     * @param closeOnFinish    should the activity end on upload complete?
     */
    public static void uploadImage(AppCompatActivity activity, StorageReference storageReference, String path, boolean closeOnFinish) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(activity, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                Log.d(UtilityPack.LOGS.FIREBASE_LOG, exception.getMessage());
                if (closeOnFinish) {
                    activity.finish();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(activity, R.string.upload_success, Toast.LENGTH_SHORT).show();
                if (closeOnFinish) {
                    activity.finish();
                }
            }
        });
    }


    /**
     * Creates a unique chat key based on two user IDs regardless if who calls this and adds the key to both users
     * Also updates the currentUser timestamp, but not the friend's timestamp unless he didn't have a chatKey
     *
     * @param myID     id of current user
     * @param friendID id of other user
     * @return chatKKey
     */
    public static String createChatKey(String myID, String friendID) {
        String chatKey = myID + friendID;
        if (friendID.compareTo(myID) > 0) {
            chatKey = friendID + myID;
        }
        return chatKey;
    }

    public static void uploadChatKey(String chatKey, String myID, String friendID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATABASE_KEYS.USERS);
        long timestamp = System.currentTimeMillis();
        ref.child(myID).child(DATABASE_KEYS.MY_CHATS).child(chatKey).setValue(timestamp);
        FirebaseAdapterManager.getFirebaseAdapterManager().addChat(chatKey);
        if (!ref.child(friendID).child(DATABASE_KEYS.MY_CHATS).child(chatKey).getKey().equals(chatKey)) {
            ref.child(friendID).child(DATABASE_KEYS.MY_CHATS).child(chatKey).setValue(timestamp);
        }
    }

    /**
     * On searching for friends, they will be first added to pending list. This handles that
     *
     * @param activity     activity context
     * @param searchValue  phone number of the user. Guaranteed to be unique from other pending/existing friends
     * @param finishOnFind on success - end the activity?
     */
    public static void addUserToPending(AppCompatActivity activity, String searchValue, boolean finishOnFind) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATABASE_KEYS.USERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.child(DATABASE_KEYS.PHONE_NUMBER).getValue(String.class).equals(searchValue)) {
                        String currentUserID = FirebaseAuth.getInstance().getUid();
                        String friendID = user.child(DATABASE_KEYS.USER_ID).getValue(String.class);
                        ref.child(friendID).child(DATABASE_KEYS.PENDING_FRIENDS).child(currentUserID).setValue(currentUserID); //could've called User.actionToCurrentUser instead, but half the work has already been done here so just continuing.
                        Toast.makeText(activity, R.string.friend_request_send, Toast.LENGTH_SHORT).show();
                        if (finishOnFind) {
                            activity.finish();
                            return;
                        }
                    }
                }
                Toast.makeText(activity, R.string.user_not_in_database, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Uploads chatMessage to the FirebaseDatabase and updates the current user's last timestamp
     *
     * @param chatMessage Message object
     * @param chatKey     chatKey between the two users
     * @param timestamp   timestamp of message creation
     * @param userID      ID of the current user
     */
    public static void uploadMessage(ChatMessage chatMessage, String chatKey, long timestamp, String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messageRef = database
                .getReference(DATABASE_KEYS.CHATS)
                .child(chatKey)
                .child(String.valueOf(timestamp));
        messageRef.setValue(chatMessage);

        DatabaseReference timeRef = database
                .getReference(DATABASE_KEYS.USERS)
                .child(userID)
                .child(DATABASE_KEYS.MY_CHATS)
                .child(chatKey);
        timeRef.setValue(timestamp);
    }


    /**
     * Stores/updates a recipe in FirebaseDatabase
     *
     * @param recipe the recipe
     */
    public static void storeRecipe(Recipe recipe) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATABASE_KEYS.RECIPES).child(recipe.getRecipeID());
        ref.setValue(recipe);
    }

    /**
     * Stores/updates user in FirebaseDatabase and FirebaseAuth
     *
     * @param user the user
     */
    public static void storeUser(User user) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATABASE_KEYS.USERS).child(user.getUserID());
        ref.setValue(user);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(user.getDisplayName())
                .build();
        firebaseUser.updateProfile(profileUpdates);
        firebaseAuth.updateCurrentUser(firebaseUser);
    }

    /**
     * Does (Action) in the current user's FirebaseDatabase
     *
     * @param action Add or remove
     * @param id     identifier of FirebaseDatabase leaf
     * @param key    determining which node to look into
     */
    public static void actionToCurrentUserDatabase(int action, String id, String key) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATABASE_KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(key)
                .child(id);
        if (action == ADD) {
            ref.setValue(id);
        } else if (action == REMOVE) {
            ref.removeValue();
        }
    }

    public interface DATABASE_KEYS {
        String USERS = "users";
        String RECIPES = "recipes";
        String CHATS = "chats";
        String MY_RECIPES = "myRecipes";
        String MY_FRIENDS = "myFriends";
        String MY_CHATS = "myChats";
        String PENDING_RECIPES = "pendingRecipes";
        String PENDING_FRIENDS = "pendingFriends";
        String PHONE_NUMBER = "phoneNumber";
        String DISPLAY_NAME = "displayName";
        String USER_ID = "userID";
        String PROFILE_IMAGE = "profileImage";
        String RECIPE_ID = "RecipeID";
        String DATE = "date";
        String DIFFICULTY = "difficulty";
        String METHOD = "method";
        String OWNER = "owner";
        String OWNER_ID = "ownerID";
        String PREP_TIME = "prepTime";
        String TITLE = "title";
        String IMAGE = "image";
        String INGREDIENTS = "ingredients";
    }

    public interface STORAGE_KEYS {
        String PROFILE_IMAGES = "profiles";
        String RECIPE_IMAGES = "recipes";
    }

    public interface FILE_KEYS {
        String JPG = ".jpg";
    }
}
