package com.dreamest.cookbookapp.utility;

import com.dreamest.cookbookapp.logic.Recipe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class TestUnit {


    public static ArrayList<Recipe> getPosts() {
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        ArrayList<Recipe> posts = new ArrayList<>();
        posts.add(new Recipe()
                .setTitle("Post 1")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );

        posts.add(new Recipe()
                .setTitle("Post 2")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );

        posts.add(new Recipe()
                .setTitle("Post 3")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );

        posts.add(new Recipe()
                .setTitle("Post 4")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );

        posts.add(new Recipe()
                .setTitle("Post 5")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );
        posts.add(new Recipe()
                .setTitle("Post 6")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );
        posts.add(new Recipe()
                .setTitle("Post 7")
                .setDate(format.format(new Date()))
                .setOwner("Test")
                .setDifficulty(4)
                .setPrepTime(300)
        );


        return posts;
    }
}
