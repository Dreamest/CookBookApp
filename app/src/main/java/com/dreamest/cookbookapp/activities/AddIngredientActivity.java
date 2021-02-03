package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Ingredient;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddIngredientActivity extends AppCompatActivity {

    private TextInputEditText add_ingredient_EDT_ingredient;
    private TextInputEditText add_ingredient_EDT_amount;
    private TextInputEditText add_ingredient_EDT_units;
    private MaterialButton add_ingredient_BTN_cancel;
    private MaterialButton add_ingredient_BTN_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        findViews();
        initViews();
    }

    private void initViews() {
        add_ingredient_EDT_ingredient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    HideUI.setNextFocus(AddIngredientActivity.this, add_ingredient_EDT_amount);
                }
                return false;
            }
        });
        add_ingredient_EDT_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    HideUI.setNextFocus(AddIngredientActivity.this, add_ingredient_EDT_units);
                }
                return false;
            }
        });
        add_ingredient_EDT_units.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    HideUI.clearFocus(AddIngredientActivity.this, add_ingredient_EDT_units);
                }
                return false;
            }
        });

        add_ingredient_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        add_ingredient_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitIngredient();
            }
        });
    }

    private void submitIngredient() {
        float amount = !add_ingredient_EDT_amount.getText().toString().equals("")?
                Float.parseFloat((add_ingredient_EDT_amount.getText().toString())) : 0;

        Ingredient ingredient = new Ingredient().
                setAmount(amount)
                .setItem(add_ingredient_EDT_ingredient.getText().toString())
                .setUnits(add_ingredient_EDT_units.getText().toString());

        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.INGREDIENT, ingredient);
        MySharedPreferences.getMsp().putBoolean(MySharedPreferences.KEYS.UPDATED_INGREDIENT, true);
        close();
    }

    private void close() {
        finish();
    }

    private void findViews() {
        add_ingredient_EDT_ingredient = findViewById(R.id.add_ingredient_EDT_ingredient);
        add_ingredient_EDT_amount = findViewById(R.id.add_ingredient_EDT_amount);
        add_ingredient_EDT_units = findViewById(R.id.add_ingredient_EDT_units);
        add_ingredient_BTN_cancel = findViewById(R.id.add_ingredient_BTN_cancel);
        add_ingredient_BTN_submit = findViewById(R.id.add_ingredient_BTN_submit);
    }
}