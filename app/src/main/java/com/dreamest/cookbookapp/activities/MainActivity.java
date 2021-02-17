package com.dreamest.cookbookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.RecipeFirebaseAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private MaterialButton main_BTN_pending;
    private TextView main_TXT_no_recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
        bindAdapter();
        observePendingRecipes();
        observeCurrentRecipes();
    }


    private void observeCurrentRecipes() {

        FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                main_TXT_no_recipes.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int recipeListSize = FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().getItemCount();
                if (recipeListSize == 0) {
                    main_TXT_no_recipes.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void handleCurrentRecipesEntry() {
        int recipeListSize = FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().getItemCount();
        if (recipeListSize == 0) {
            main_TXT_no_recipes.setVisibility(View.VISIBLE);
        } else {
            main_TXT_no_recipes.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //specifically upon returning from profileActivity
            preformLogout();
            return;
        }
        handleCurrentRecipesEntry();
        handlePendingRecipesEntry();
    }


    private void preformLogout() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void bindAdapter() {
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().setClickListener(new RecipeFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openRecipeActivity(position);
            }
        });
        main_LST_recipes.setAdapter(FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter());

    }

    private void observePendingRecipes() {

        FirebaseAdapterManager.getFirebaseAdapterManager().getPendingRecipeFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingRecipeFirebaseAdapter().getItemCount();
                String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_recipes);
                main_BTN_pending.setText(message);
                main_BTN_pending.setVisibility(View.VISIBLE);

                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingRecipeFirebaseAdapter().getItemCount();
                if (pendingSize == 0) {
                    main_BTN_pending.setVisibility(View.GONE);
                } else {
                    String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_recipes);
                    main_BTN_pending.setText(message);
                    main_BTN_pending.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void handlePendingRecipesEntry() {
        int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingRecipeFirebaseAdapter().getItemCount();
        if(pendingSize > 0) {
            String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_recipes);
            main_BTN_pending.setText(message);
            main_BTN_pending.setVisibility(View.VISIBLE);
        } else {
            main_BTN_pending.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        Glide
                .with(this)
                .load(R.drawable.background_simple_waves)
                .fitCenter()
                .into(main_IMG_background);

        main_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecipe();
            }
        });

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
        startActivity(myIntent);
    }

    private void toProfile() {
        Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(myIntent);

    }

    private void toFriendsList() {
        Intent myIntent = new Intent(MainActivity.this, FriendsListActivity.class);
        startActivity(myIntent);

    }

    private void addNewRecipe() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
        startActivity(myIntent);
    }

    private void findViews() {
        main_BTN_add = findViewById(R.id.main_BTN_add);
        main_LST_recipes = findViewById(R.id.main_LST_recipes);
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_BTN_pending = findViewById(R.id.main_BTN_pending);
        main_TXT_no_recipes = findViewById(R.id.main_TXT_no_recipes);
    }

    private void openRecipeActivity(int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.RECIPES)
                .child(FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().getItem(position));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, recipe);
                Intent myIntent = new Intent(MainActivity.this, RecipeActivity.class);
                startActivity(myIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }
}