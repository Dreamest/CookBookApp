package com.dreamest.cookbookapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.fragments.CookbookFragment;
import com.dreamest.cookbookapp.fragments.FriendslistFragment;
import com.dreamest.cookbookapp.fragments.ProfileFragment;

public class MainActivity extends BaseActivity implements GestureDetector.OnGestureListener {
    private CookbookFragment cookbookFragment;
    private ProfileFragment profileFragment;
    private FriendslistFragment friendslistFragment;
    private ImageView test_IMG_background;
    private ImageButton main_BTN_profile;
    private ImageButton main_BTN_cookbook;
    private ImageButton main_BTN_friendslist;

    private enum WINDOW_STATE {
        PROFILE,
        COOKBOOK,
        FRIENDSLIST
    }

    ;
    private float x1, x2, y1, y2;
    private static final int MIN_DISTANCE = 400;
    private GestureDetector gestureDetector;

    private WINDOW_STATE windowState = WINDOW_STATE.COOKBOOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        changeToFragment(windowState, 0, 0);
        gestureDetector = new GestureDetector(this, this);
    }

    private void initViews() {
        cookbookFragment = new CookbookFragment();
        profileFragment = new ProfileFragment();
        friendslistFragment= new FriendslistFragment();

        Glide
                .with(this)
                .load(R.drawable.background_simple_waves)
                .into(test_IMG_background);

        main_BTN_friendslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(windowState == WINDOW_STATE.FRIENDSLIST) {
                    return;
                }
                int in = R.anim.enter_left_to_right;
                int out = R.anim.exit_left_to_right;
                windowState = WINDOW_STATE.FRIENDSLIST;
                changeToFragment(windowState, in, out);
            }
        });

        main_BTN_cookbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int in, out;
                if(windowState == WINDOW_STATE.COOKBOOK) {
                    return;
                } else if(windowState == WINDOW_STATE.PROFILE) {
                    in = R.anim.enter_left_to_right;
                    out = R.anim.exit_left_to_right;
                } else { //friendslist
                    in = R.anim.enter_right_to_left;
                    out = R.anim.exit_right_to_left;

                }
                windowState = WINDOW_STATE.COOKBOOK;
                changeToFragment(windowState, in, out);
            }
        });

        main_BTN_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(windowState == WINDOW_STATE.PROFILE) {
                    return;
                }
                int in = R.anim.enter_right_to_left;
                int out = R.anim.exit_right_to_left;
                windowState = WINDOW_STATE.PROFILE;
                changeToFragment(windowState, in, out);
            }
        });

    }

    private void findViews() {
        test_IMG_background = findViewById(R.id.main_IMG_background);
        main_BTN_profile = findViewById(R.id.main_BTN_profile);
        main_BTN_cookbook = findViewById(R.id.main_BTN_cookbook);
        main_BTN_friendslist = findViewById(R.id.main_BTN_friendslist);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x1 = ev.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = ev.getX();
                float valueX = x2 - x1;
                int in, out;
                if (Math.abs(valueX) > MIN_DISTANCE) {
                    if (x2 > x1) { //right
                        in = R.anim.enter_left_to_right;
                        out = R.anim.exit_left_to_right;
                        if (windowState == WINDOW_STATE.COOKBOOK) {
                            windowState = WINDOW_STATE.FRIENDSLIST;
                        } else if (windowState == WINDOW_STATE.PROFILE) {
                            windowState = WINDOW_STATE.COOKBOOK;
                        }
                    } else { //left
                        in = R.anim.enter_right_to_left;
                        out = R.anim.exit_right_to_left;
                        if (windowState == WINDOW_STATE.COOKBOOK) {
                            windowState = WINDOW_STATE.PROFILE;
                        } else if (windowState == WINDOW_STATE.FRIENDSLIST) {
                            windowState = WINDOW_STATE.COOKBOOK;
                        }
                    }
                    changeToFragment(windowState, in, out);
                }
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void changeToFragment(WINDOW_STATE windowState, int enterAnimation, int exitAnimation) {
        if(windowState == WINDOW_STATE.COOKBOOK) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(enterAnimation, exitAnimation)
                    .replace(R.id.main_LAY_fragment_holder, cookbookFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            drawButtons(main_BTN_cookbook);
        } else if(windowState == WINDOW_STATE.FRIENDSLIST) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(enterAnimation, exitAnimation)
                    .replace(R.id.main_LAY_fragment_holder, friendslistFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            drawButtons(main_BTN_friendslist);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(enterAnimation, exitAnimation)
                    .replace(R.id.main_LAY_fragment_holder, profileFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            drawButtons(main_BTN_profile);
        }
    }

    private void drawButtons(ImageButton active) {
        main_BTN_friendslist.setBackgroundResource(R.color.teal);
        main_BTN_cookbook.setBackgroundResource(R.color.teal);
        main_BTN_profile.setBackgroundResource(R.color.teal);

        active.setBackgroundResource(R.color.teal_deep);

    }
}