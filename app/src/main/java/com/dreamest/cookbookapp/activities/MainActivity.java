package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.RecipeAdapter;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {

    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private HashMap<String, Recipe> myRecipesMap;// = TestUnit.getPosts();
    private ArrayList<Recipe> myRecipesList;
    private RecipeAdapter recipeAdapter;
    private TextView main_TXT_no_recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        myRecipesMap = new HashMap<>();
        myRecipesList = new ArrayList<>();

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFromDatabase(); //onResume so it'll load a new recipe when adding one.
    }


    /**
     * Loads all recipes that belong to the current user to a recyclerView
     */
    private void loadFromDatabase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.USERS)
                .child(firebaseUser.getUid())
                .child(UtilityPack.MY_RECIPES);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> recipeIds = snapshot.getChildren();
                Log.d("dddd", "children count = " + snapshot.getChildrenCount());
                if (recipeIds != null) {
                    main_TXT_no_recipes.setVisibility(View.GONE);
                    for(DataSnapshot id: recipeIds) {
                        loadRecipe(id, database);
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

    /**
     * Loads a recipe from firebase into recyclerView
     * @param id Recipe ID
     * @param database firebase database
     */
    private void loadRecipe(DataSnapshot id, FirebaseDatabase database) {
        DatabaseReference recipeRef = database.getReference(UtilityPack.RECIPES)
                .child(id.getValue(String.class));
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("dddd", "" + snapshot.getValue(Recipe.class));
                myRecipesMap.put(id.getValue(String.class), snapshot.getValue(Recipe.class));
                Log.d("dddd", myRecipesMap.size() + "");
                if (recipeAdapter != null) {
                    reAttachAdapater();
                } else {
                    initAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void reAttachAdapater() {
        myRecipesList = new ArrayList<>(myRecipesMap.values());
        recipeAdapter = new RecipeAdapter(MainActivity.this, myRecipesList);
        main_LST_recipes.setAdapter(recipeAdapter);
    }

    private void initAdapter() {
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this, myRecipesList);

        recipeAdapter.setClickListener(new RecipeAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openRecipeActivity(position);
            }
        });
        Log.d("dddd", "In adapater " + myRecipesMap.size());

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

    }

    private void toProfile() {
        Log.d("dddd", "going to profile");
        // TODO: 1/29/21 implement
    }

    private void toFriendsList() {
        Log.d("dddd", "going to friendslist");
        // TODO: 1/29/21 implement

    }

    private void addNewRecipe() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
        startActivity(myIntent);
        // TODO: 1/27/21 implement: Send to create post activity.
    }

    private void findViews() {
        main_BTN_add = findViewById(R.id.main_BTN_add);
        main_LST_recipes = findViewById(R.id.main_LST_recipes);
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_TXT_no_recipes = findViewById(R.id.main_TXT_no_recipes);
    }

    private void openRecipeActivity(int position) {
        Log.d("dddd", String.format("Post %d pressed.", position));
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, myRecipesList.get(position));
        Intent myIntent = new Intent(this, RecipeActivity.class);
        startActivity(myIntent);
    }
}