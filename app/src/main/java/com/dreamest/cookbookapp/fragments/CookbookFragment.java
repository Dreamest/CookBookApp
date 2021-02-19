package com.dreamest.cookbookapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.activities.EditRecipeActivity;
import com.dreamest.cookbookapp.activities.PendingRecipesActivity;
import com.dreamest.cookbookapp.activities.RecipeActivity;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.adapters.RecipeFirebaseAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CookbookFragment extends Fragment {
    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private MaterialButton main_BTN_pending;
    private TextView main_TXT_no_recipes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);
        findViews(view);
        initViews();
        bindAdapter();
        observePendingRecipes();
        observeCurrentRecipes();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleCurrentRecipesEntry();
        handlePendingRecipesEntry();
    }

    private void handleCurrentRecipesEntry() {
        int recipeListSize = FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().getItemCount();
        if (recipeListSize == 0) {
            main_TXT_no_recipes.setVisibility(View.VISIBLE);
        } else {
            main_TXT_no_recipes.setVisibility(View.GONE);
        }
    }

    private void handlePendingRecipesEntry() {
        int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingRecipeFirebaseAdapter().getItemCount();
        if (pendingSize > 0) {
            String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_recipes);
            main_BTN_pending.setText(message);
            main_BTN_pending.setVisibility(View.VISIBLE);
        } else {
            main_BTN_pending.setVisibility(View.GONE);
        }
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

    private void bindAdapter() {
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter().setClickListener(new RecipeFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openRecipeActivity(position);
            }
        });
        main_LST_recipes.setAdapter(FirebaseAdapterManager.getFirebaseAdapterManager().getRecipeFirebaseAdapter());
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
                Intent myIntent = new Intent(getActivity(), RecipeActivity.class);
                startActivity(myIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initViews() {
        main_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecipe();
            }
        });

        main_BTN_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPendingRecipes();
            }
        });
    }

    private void addNewRecipe() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(getActivity(), EditRecipeActivity.class);
        startActivity(myIntent);
    }

    private void openPendingRecipes() {
        Intent myIntent = new Intent(getActivity(), PendingRecipesActivity.class);
        startActivity(myIntent);
    }

    private void findViews(View view) {
        main_BTN_add = view.findViewById(R.id.cookbook_BTN_add);
        main_LST_recipes = view.findViewById(R.id.cookbook_LST_recipes);
        main_BTN_pending = view.findViewById(R.id.cookbook_BTN_pending);
        main_TXT_no_recipes = view.findViewById(R.id.cookbook_TXT_no_recipes);
    }
}
