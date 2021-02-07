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
import java.util.Iterator;

public class MainActivity extends BaseActivity {

    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private TextView main_TXT_no_recipes;
    private MaterialButton main_BTN_pending;
    private ArrayList<Recipe> pendingRecipes;
    private ArrayList<Recipe> myRecipesList;// = TestUnit.getPosts();
    private User currentUser;

    private final int PENDING_RECIPES = 123;
    private final int MY_RECIPES = 345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            preformLogout();
            return;
        }
        //Since we use lists, on reloading them they need to be emptied first.
        myRecipesList = new ArrayList<>();
        pendingRecipes = new ArrayList<>();

        //onResume so it'll update on returning to the activity
        loadCurrentUser();
        loadCurrentUserRecipesFromDatabase();
        loadPendingRecipes();
    }


    private void preformLogout() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    /**
     * Loads all recipes that belong to the current user to a recyclerView
     */
    private void loadCurrentUserRecipesFromDatabase() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.KEYS.MY_RECIPES);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    main_TXT_no_recipes.setVisibility(View.GONE);
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    while(iterator.hasNext()) {
                        DataSnapshot recipeSnapshot = iterator.next();
                        loadRecipe(recipeSnapshot, database, MY_RECIPES, !iterator.hasNext());
                    }

                } else { //No recipes
                    main_TXT_no_recipes.setVisibility(View.VISIBLE);
                    initAdapter(); //The adapter needs to be initialized if there are no recipes, too.
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
                loadRecipe(snapshot, database, PENDING_RECIPES, false);
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
        for(Recipe recipe: pendingRecipes) {
            if(recipe.getRecipeID().equals(id.getValue())) {
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
    //This functions needs to be here and not in Recipe to be able to handle the adapter properly.
    private void loadRecipe(DataSnapshot id, FirebaseDatabase database, int loadTo, boolean last) {
        DatabaseReference recipeRef = database.getReference(UtilityPack.KEYS.RECIPES)
                .child(id.getValue(String.class));
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    myRecipesList.add(snapshot.getValue(Recipe.class));
                    if(last) {
                        initAdapter();
                    }
                } else if(loadTo == PENDING_RECIPES) {
                    pendingRecipes.add(snapshot.getValue(Recipe.class));
                    String message = getString(R.string.you_have) + " " + pendingRecipes.size() + " " + getString(R.string.pending_recipes);
                    main_BTN_pending.setText(message);
                    main_BTN_pending.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void initAdapter() {
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
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.MY_RECIPES_ARRAY, pendingRecipes);
        startActivity(myIntent);
    }

    private void loadCurrentUser() { // TODO: 2/5/21 might be smart to make static and move
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void toProfile() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.USER, currentUser);
        Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(myIntent);
    }

    private void toFriendsList() {
        Intent myIntent = new Intent(MainActivity.this, FriendsListActivity.class);
        startActivity(myIntent);
    }

    private void addNewRecipe() {
        Log.d("dddd", "New recipe clicked");
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
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
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.USER, currentUser);
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, myRecipesList.get(position));
        Intent myIntent = new Intent(this, RecipeActivity.class);
        startActivity(myIntent);
    }
}