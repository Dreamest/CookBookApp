package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.adapters.RecipeAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {

    // TODO: 2/4/21 check if hashMap can be removed.
    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private HashMap<String, Recipe> myRecipesMap;// = TestUnit.getPosts();
    private ArrayList<Recipe> myRecipesList;// = TestUnit.getPosts();
    private TextView main_TXT_no_recipes;
    private MaterialButton main_BTN_pending;
    private ArrayList<Recipe> pendingRecipes;
    private final int PENDING_RECIPES = 123;
    private final int MY_RECIPES = 345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRecipesMap = new HashMap<>();
        myRecipesList = new ArrayList<>();
        pendingRecipes = new ArrayList<>();

        findViews();
        loadPendingRecipes();
        initViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            preformLogout();
        }
        loadUserRecipesFromDatabase(); //onResume so it'll load a new recipe when adding one.
    }

    private void preformLogout() {
        MySharedPreferences.getMsp().putBoolean(MySharedPreferences.KEYS.LOGOUT, MySharedPreferences.KEYS.STAY_LOGGED); //logically might fit better in LoginActivity onCreate, but this saves running this line every login
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    /**
     * Loads all recipes that belong to the current user to a recyclerView
     */
    private void loadUserRecipesFromDatabase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.KEYS.MY_RECIPES);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    main_TXT_no_recipes.setVisibility(View.GONE);
                    Iterable<DataSnapshot> recipeIds = snapshot.getChildren();
                    for(DataSnapshot id: recipeIds) {
                        loadRecipe(id, database, MY_RECIPES);
                    }
                } else { //No recipes
                    main_TXT_no_recipes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadPendingRecipes() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.KEYS.PENDING_RECIPES);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadRecipe(snapshot, database, PENDING_RECIPES);
                String message = getString(R.string.you_have) + " " + pendingRecipes.size() + " " + getString(R.string.pending_recipes);
                main_BTN_pending.setVisibility(View.VISIBLE);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                removePending(snapshot);
                if(pendingRecipes.size() == 0) {
                    main_BTN_pending.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void removePending(DataSnapshot id) {
//        pendingRecipes.removeIf(recipe -> recipe.getRecipeID().equals(id)); //requires SDK 26 ask Guy
        for(Recipe recipe: pendingRecipes) {
            if(recipe.getRecipeID().equals(id)) {
                pendingRecipes.remove(recipe);
                return;
            }
        }
    }


    /**
     * Loads a recipe from firebase into recyclerView
     * @param id Recipe ID
     * @param database firebase database
     */
    //This functions needs to be here and not in Recipe to be able to handle the adapter synchronically.
    private void loadRecipe(DataSnapshot id, FirebaseDatabase database, int loadTo) {
        DatabaseReference recipeRef = database.getReference(UtilityPack.KEYS.RECIPES)
                .child(id.getValue(String.class));
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Recipe recipe = new Recipe()
//                        .setRecipeID(snapshot.child(UtilityPack.KEYS.RECIPE_ID).getValue(String.class))
//                        .setDate(snapshot.child(UtilityPack.KEYS.DATE).getValue(String.class))
//                        .setDifficulty(snapshot.child(UtilityPack.KEYS.DIFFICULTY).getValue(Integer.class))
//                        .setImage(snapshot.child(UtilityPack.KEYS.IMAGE).getValue(StorageReference.class))
//                        .setIngredients(getListFromDatabase(snapshot.child(UtilityPack.KEYS.INGREDIENTS)))
//                        .setMethod(snapshot.child(UtilityPack.KEYS.METHOD).getValue(String.class))
//                        .setOwner(snapshot.child(UtilityPack.KEYS.OWNER).getValue(String.class))
//                        .setOwnerID(snapshot.child(UtilityPack.KEYS.OWNER_ID).getValue(String.class))
//                        .setPrepTime(snapshot.child(UtilityPack.KEYS.PREP_TIME).getValue(Integer.class))
//                        .setTitle(snapshot.child(UtilityPack.KEYS.TITLE).getValue(String.class));
//                myRecipesList.add(recipe);
                if(loadTo == MY_RECIPES) {
                    myRecipesMap.put(id.getValue(String.class), snapshot.getValue(Recipe.class));
                } else if(loadTo == PENDING_RECIPES) {
                    pendingRecipes.add(snapshot.getValue(Recipe.class));
                }
                initAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void initAdapter() {
        myRecipesList = new ArrayList<>(myRecipesMap.values());
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        RecipeAdapter recipeAdapter = new RecipeAdapter(this, myRecipesList);

        recipeAdapter.setClickListener(new RecipeAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openRecipeActivity(position);
            }
        });
        main_LST_recipes.setAdapter(recipeAdapter);
    }

    private void initViews() {
        initAdapter();

        main_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecipe();
            }
        });

        Glide.with(this)
                .load(UtilityPack.randomBackground())
                .centerCrop()
                .into(main_IMG_background);

        main_LST_recipes.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                toFriendsList();
            }

            public void onSwipeLeft() {
                toProfile();
            }

        });

        main_BTN_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPendingRecipes();
            }
        });
    }

    private void openPendingRecipes() {
        Intent myIntent = new Intent(this, PendingRecipesActivity.class);
        myIntent.putExtra(MySharedPreferences.KEYS.RECIPE_COUNT, myRecipesList.size());
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPES_LIST, pendingRecipes);
        startActivity(myIntent);
    }

    private void loadUserToActivity(Class goToClass) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = new User()
//                        .setPhoneNumber(snapshot.child(UtilityPack.KEYS.PHONE_NUMBER).getValue(String.class))
//                        .setDisplayName(snapshot.child(UtilityPack.KEYS.DISPLAY_NAME).getValue(String.class))
//                        .setUserID(snapshot.child(UtilityPack.KEYS.USER_ID).getValue(String.class))
//                        .setMyRecipes(getListFromDatabase(snapshot.child(UtilityPack.KEYS.MY_RECIPES)))
//                        .setMyFriends(getListFromDatabase(snapshot.child(UtilityPack.KEYS.MY_FRIENDS)))
//                        .setMyChats(getListFromDatabase(snapshot.child(UtilityPack.KEYS.MY_CHATS)))
//                        .setProfileImage(snapshot.child(UtilityPack.KEYS.PROFILE_IMAGE).getValue(StorageReference.class));
                User user = snapshot.getValue(User.class);
                MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.USER, user);
                Intent myIntent = new Intent(MainActivity.this, goToClass);
                startActivity(myIntent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void toProfile() {
        loadUserToActivity(ProfileActivity.class);
    }

    private <T> ArrayList<T> getListFromDatabase(DataSnapshot dataSnapshot) {
        ArrayList<T> list = new ArrayList<>();
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            list.add((T)postSnapshot.getValue());
        }
        return list;
    }

    private void toFriendsList() {
        loadUserToActivity(FriendsListActivity.class);
    }

    private void addNewRecipe() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
        myIntent.putExtra(MySharedPreferences.KEYS.RECIPE_COUNT, myRecipesList.size());
        startActivity(myIntent);
    }

    private void findViews() {
        main_BTN_add = findViewById(R.id.main_BTN_add);
        main_LST_recipes = findViewById(R.id.main_LST_recipes);
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_TXT_no_recipes = findViewById(R.id.main_TXT_no_recipes);
        main_BTN_pending = findViewById(R.id.main_BTN_pending);
    }

    private void openRecipeActivity(int position) {
        Log.d("dddd", String.format("Post %d pressed.", position));
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, myRecipesList.get(position));
        Intent myIntent = new Intent(this, RecipeActivity.class);
        startActivity(myIntent);
    }
}