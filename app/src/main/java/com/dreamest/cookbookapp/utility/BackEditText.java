package com.dreamest.cookbookapp.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BackEditText extends androidx.appcompat.widget.AppCompatEditText
{
    public BackEditText(@NonNull Context context) {
        super(context);
    }

    public BackEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            HideUI.clearFocus((AppCompatActivity)getContext(), this);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}