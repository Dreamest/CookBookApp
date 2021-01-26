package com.dreamest.cookbookapp.utility;

import com.dreamest.cookbookapp.R;

import java.util.Random;

public class UtilityPack {
    private static int[] allBackgrounds = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4};

    public static int randomBackground() {
        return allBackgrounds[new Random().nextInt(4)];
    }
}
