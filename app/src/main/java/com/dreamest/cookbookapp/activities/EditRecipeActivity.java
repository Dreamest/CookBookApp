package com.dreamest.cookbookapp.activities;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.IngredientAdapter;
import com.dreamest.cookbookapp.logic.Ingredient;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.gildaswise.horizontalcounter.HorizontalCounter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opensooq.supernova.gligar.GligarPicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditRecipeActivity extends BaseActivity {
    private TextInputEditText edit_EDT_title;
    private MaterialButton edit_BTN_add_ingredient;
    private TextInputEditText edit_EDT_method;
    private ImageView edit_IMG_image;
    private MaterialButton edit_BTN_submit;
    private RecyclerView edit_LST_ingredients;
    private ImageView[] stars;
    private HorizontalCounter edit_CTR_prepTime;
    private ScrollView edit_LAY_scroll;
    private ProgressBar edit_PROGBAR_spinner;

    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private int difficulty;
    private boolean imageChanged;
    private String tempPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        findViews();
        loadRecipe();
        initViews();
    }

    private void focusOnView(ScrollView sv, View v){
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollTo(0,v.getHeight());
            }
        });
    }

    private void loadRecipe() {
        recipe = (Recipe) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.RECIPE, new Recipe(), Recipe.class);
        if(recipe.getRecipeID().trim().equals("")) { //New recipe. Ensures it has an id for image loading
            recipe.setRecipeID(UtilityPack.CreateRecipeID(FirebaseAuth.getInstance().getUid()));
        }
        ingredients = recipe.getIngredients();
        difficulty = recipe.getDifficulty();
        edit_EDT_title.setText(recipe.getTitle());
        edit_EDT_method.setText(recipe.getMethod());
        changeDifficulty(recipe.getDifficulty());
        if(!recipe.getImagePath().trim().equals("")) {
            FirebaseTools.downloadImage(this, recipe.getImagePath(), recipe.getRecipeID(), FirebaseTools.FILE_KEYS.JPG,
                    edit_IMG_image, ContextCompat.getDrawable(this, R.drawable.ic_loading), R.drawable.ic_no_image);
        } else {
            edit_IMG_image.setImageResource(R.drawable.ic_camera);
        }

        edit_CTR_prepTime.setCurrentValue((double) recipe.getPrepTime());
        edit_CTR_prepTime.setDisplayingInteger(true);
        /*
        Code for HorizontalCounter is kinda weird. setCurrentValue updates the stored value but not the view.
        Calling setDisplayInteger activates a private function that updates the view. We want it to be int, anyways.
        */

        loadIngredientsAdapter();

    }

    private void initViews() {
        edit_IMG_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideUI.clearFocus(EditRecipeActivity.this, edit_EDT_method);
                setImage();
            }
        });

        edit_EDT_method.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                focusOnView(edit_LAY_scroll, edit_EDT_method);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(EditRecipeActivity.this, edit_EDT_method);
                }
                return false;
            }
        });

        edit_EDT_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(EditRecipeActivity.this, edit_EDT_title);
                }
                return false;
            }
        });

        edit_BTN_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewIngredient();
            }
        });


        edit_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRecipe();
            }
        });

        for (ImageView star : stars) {
            star.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    HideUI.clearFocus(EditRecipeActivity.this, edit_EDT_method);
                    changeDifficulty((int) star.getTag());
                }
            });
        }
    }

    private void setImage() {
        new GligarPicker()
                .requestCode(UtilityPack.REQUEST_CODES.GILGAR)
                .withActivity(this)
                .limit(1) //maximum amount of images picked
                .show();
    }

    private void changeDifficulty(int difficulty) {
        for (ImageView star : stars) {
            if ((int) star.getTag() <= difficulty) {
                star.setImageResource(R.drawable.ic_full_star);
            } else {
                star.setImageResource(R.drawable.ic_empty_star);
            }
        }
        this.difficulty = difficulty;
    }

    private void loadIngredientsAdapter() {
        edit_LST_ingredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, ingredients, IngredientAdapter.REMOVE_BUTTON);

        ingredientAdapter.setClickListener(new IngredientAdapter.ItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
                HideUI.clearFocus(EditRecipeActivity.this, edit_EDT_method);
                ingredients.remove(position);
                updateAdapter(ingredientAdapter); //Needs to be reapplied to update the view
            }
        });
        updateAdapter(ingredientAdapter);
    }

    private void updateAdapter(IngredientAdapter ingredientAdapter) {
        edit_LST_ingredients.setAdapter(ingredientAdapter);
        updateAddButton();
    }

    private void updateAddButton() {
        if (recipe.getIngredients().size() == 0) {
            edit_BTN_add_ingredient.setText(R.string.add_first_ingredient);
        } else {
            edit_BTN_add_ingredient.setText(R.string.add_another_ingredient);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MySharedPreferences.getMsp().getBoolean(MySharedPreferences.KEYS.UPDATED_INGREDIENT, false)) {
            MySharedPreferences.getMsp().putBoolean(MySharedPreferences.KEYS.UPDATED_INGREDIENT, false);
            ingredients.add((Ingredient) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.INGREDIENT, new Ingredient(), Ingredient.class));
            loadIngredientsAdapter();

        }
    }

    private void submitRecipe() {
        if(!edit_EDT_title.getText().toString().trim().equals("")) {
            updateRecipe();
            MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, recipe);
            FirebaseTools.storeRecipe(recipe);
            FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.ADD, recipe.getRecipeID(), FirebaseTools.DATABASE_KEYS.MY_RECIPES);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage
                    .getReference(FirebaseTools.STORAGE_KEYS.RECIPE_IMAGES)
                    .child(recipe.getOwnerID())
                    .child(recipe.getRecipeID());
            Toast.makeText(this, R.string.uploading, Toast.LENGTH_SHORT).show();
            if(imageChanged) {
                FirebaseTools.uploadImage(this, storageReference, tempPath, true);
                playLoading();
            } else {
                finish();
            }
//        finish(); activity will finish when upload is done
        } else {
            Toast.makeText(this, R.string.warn_no_title, Toast.LENGTH_SHORT).show();
        }
    }

    private void playLoading() {
        edit_BTN_submit.setVisibility(View.GONE);
        edit_PROGBAR_spinner.setVisibility(View.VISIBLE);
    }

    private void updateRecipe() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (recipe.getRecipeID().trim().equals("")) {
            recipe.setRecipeID(UtilityPack.CreateRecipeID(firebaseUser.getUid()));
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference(FirebaseTools.STORAGE_KEYS.RECIPE_IMAGES).child(firebaseUser.getUid()).child(recipe.getRecipeID());

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        recipe.setDate(format.format(new Date()));
        recipe.setDifficulty(difficulty);
        recipe.setImagePath(storageReference.getPath());
        recipe.setIngredients(ingredients);
        recipe.setMethod(edit_EDT_method.getText().toString());
        recipe.setOwnerID(firebaseUser.getUid());
        recipe.setPrepTime(edit_CTR_prepTime.getCurrentValue().intValue());
        recipe.setDifficulty(difficulty);
        recipe.setTitle(edit_EDT_title.getText().toString());

    }

    private void addNewIngredient() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.INGREDIENT, new Ingredient()); //Reset the held ingredient
        Intent myIntent = new Intent(this, AddIngredientActivity.class);
        startActivity(myIntent);
    }

    private void findViews() {
        stars = new ImageView[5];
        edit_EDT_title = findViewById(R.id.edit_EDT_title);
        edit_BTN_add_ingredient = findViewById(R.id.edit_BTN_add_ingredient);
        edit_EDT_method = findViewById(R.id.edit_EDT_method);
        edit_EDT_method.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edit_IMG_image = findViewById(R.id.edit_IMG_image);
        edit_BTN_submit = findViewById(R.id.edit_BTN_submit);
        edit_LST_ingredients = findViewById(R.id.edit_LST_ingredients);
        stars[0] = findViewById(R.id.edit_IMG_star1);
        stars[0].setTag(1);
        stars[1] = findViewById(R.id.edit_IMG_star2);
        stars[1].setTag(2);
        stars[2] = findViewById(R.id.edit_IMG_star3);
        stars[2].setTag(3);
        stars[3] = findViewById(R.id.edit_IMG_star4);
        stars[3].setTag(4);
        stars[4] = findViewById(R.id.edit_IMG_star5);
        stars[4].setTag(5);
        edit_CTR_prepTime = findViewById(R.id.edit_CTR_prepTime);
        edit_LAY_scroll = findViewById(R.id.edit_LAY_scroll);
        edit_PROGBAR_spinner = findViewById(R.id.edit_PROGBAR_spinner);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode){
            case UtilityPack.REQUEST_CODES.GILGAR : {
                File image = new File(data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT)[0]);
                UtilityPack.cropImage(this, image, recipe.getRecipeID());
                break;
            }

            case UtilityPack.REQUEST_CODES.UCROP : {
                tempPath = UCrop.getOutput(data).getPath();
                UtilityPack.loadUCropResult(this, tempPath, edit_IMG_image, R.drawable.ic_no_image);
                imageChanged = true;
                break;
            }
        }
    }
}