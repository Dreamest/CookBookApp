package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.RecipeAdapter;
import com.dreamest.cookbookapp.utility.TestUnit;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private ArrayList<Recipe> myRecipes = TestUnit.getPosts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();

    }

    private void initViews() {
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        RecipeAdapter recipeAdapter = new RecipeAdapter(this, myRecipes);

        recipeAdapter.setClickListener(new RecipeAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openPostActivity(position);
            }
        });
//
        main_LST_recipes.setAdapter(recipeAdapter);

        main_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPost();
            }
        });

        Glide.with(this)
                .load(UtilityPack.randomBackground())
                .centerCrop()
                .into(main_IMG_background);

    }

    private void addNewPost() {
        Log.d("dddd", "New post button clicked");
        // TODO: 1/27/21 implement: Send to create post activity.
    }

    private void findViews() {
        main_BTN_add = findViewById(R.id.main_BTN_add);
        main_LST_recipes = findViewById(R.id.main_LST_recipes);
        main_IMG_background = findViewById(R.id.main_IMG_background);
    }

    private void openPostActivity(int position) {
        Log.d("dddd", String.format("Post %d pressed.", position));
        // TODO: 1/27/21 implement: open post in position position
    }
}