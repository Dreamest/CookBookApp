package com.dreamest.cookbookapp.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dreamest.cookbookapp.logic.ChatMessage;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseAdapterManager {
    private static FirebaseAdapterManager firebaseAdapterManager;
    private RecipeFirebaseAdapter recipeFirebaseAdapter;
    private PendingRecipeFirebaseAdapter pendingRecipeFirebaseAdapter;
    private PendingFriendsFirebaseAdapter pendingFriendsFirebaseAdapter;
    private FriendFirebaseAdapter friendFirebaseAdapter;
    private HashMap<String, ChatFirebaseAdapter> chatAdapters;
    private String uid;

    private FirebaseAdapterManager(String uid) {
        this.uid = uid;
        initRecipes();
        initPendingRecipes();
        initFriends();
        initPendingFriends();
        initChats();
        startListeningAll();
    }

    public void addChat(String chatKey) {
        if(chatAdapters.containsKey(chatKey)) {
            return;
        }
        DatabaseReference chatRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.CHATS)
                .child(chatKey);
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(chatRoot, ChatMessage.class)
                .build();
        ChatFirebaseAdapter adapter = new ChatFirebaseAdapter(options);
        adapter.startListening();
        chatAdapters.put(chatKey, adapter);
    }

    private void initChats() {
        chatAdapters = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(uid)
                .child(FirebaseTools.DATABASE_KEYS.MY_CHATS);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chatKey : snapshot.getChildren()) {
                    String key = chatKey.getKey();
                    addChat(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });

    }

    private void initFriends() {
        DatabaseReference friendslistRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(uid)
                .child(FirebaseTools.DATABASE_KEYS.MY_FRIENDS);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(friendslistRoot, String.class)
                .build();
        friendFirebaseAdapter = new FriendFirebaseAdapter(options);
    }

    private void initPendingFriends() {
        DatabaseReference pendingFriendsRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(uid)
                .child(FirebaseTools.DATABASE_KEYS.PENDING_FRIENDS);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingFriendsRoot, String.class)
                .build();
        pendingFriendsFirebaseAdapter = new PendingFriendsFirebaseAdapter(options);
    }

    public static void init(String uid) {
        if (firebaseAdapterManager == null) {
            firebaseAdapterManager = new FirebaseAdapterManager(uid);
        }
    }

    private void initPendingRecipes() {
        DatabaseReference pendingRecipesRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(uid)
                .child(FirebaseTools.DATABASE_KEYS.PENDING_RECIPES);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingRecipesRoot, String.class)
                .build();
        pendingRecipeFirebaseAdapter = new PendingRecipeFirebaseAdapter(options);
    }

    private void initRecipes() {
        DatabaseReference recipesRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(uid)
                .child(FirebaseTools.DATABASE_KEYS.MY_RECIPES);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(recipesRoot, String.class)
                .build();
        recipeFirebaseAdapter = new RecipeFirebaseAdapter(options);
    }

    public static FirebaseAdapterManager getFirebaseAdapterManager() {
        return firebaseAdapterManager;
    }

    public RecipeFirebaseAdapter getRecipeFirebaseAdapter() {
        return recipeFirebaseAdapter;
    }

    public PendingRecipeFirebaseAdapter getPendingRecipeFirebaseAdapter() {
        return pendingRecipeFirebaseAdapter;
    }

    public FriendFirebaseAdapter getFriendFirebaseAdapter() {
        return friendFirebaseAdapter;
    }

    public PendingFriendsFirebaseAdapter getPendingFriendsFirebaseAdapter() {
        return pendingFriendsFirebaseAdapter;
    }

    public ChatFirebaseAdapter getChatFirebaseAdapter(String chatKey) {
        return chatAdapters.get(chatKey);
    }

    public void startListeningAll() {
        recipeFirebaseAdapter.startListening();
        pendingRecipeFirebaseAdapter.startListening();
        pendingFriendsFirebaseAdapter.startListening();
        friendFirebaseAdapter.startListening();
    }

    public void destroy() {
        firebaseAdapterManager = null;
    }

    public void stopListeningAll() {
        recipeFirebaseAdapter.stopListening();
        pendingRecipeFirebaseAdapter.stopListening();
        pendingFriendsFirebaseAdapter.stopListening();
        friendFirebaseAdapter.stopListening();
        for (ChatFirebaseAdapter adapter : chatAdapters.values()) {
            adapter.stopListening();
        }
    }
}
