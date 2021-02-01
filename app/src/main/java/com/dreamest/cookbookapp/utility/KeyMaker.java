package com.dreamest.cookbookapp.utility;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KeyMaker {
    private static final String RECIPE_PREFIX = "R";
    private static final String KEYMAKER = "KeyMaker";
    private static final String RID = "rid";
    private static String value  = "";

    public static String getNewRecipeKey() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(KEYMAKER).child(RID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ridValue = ((Long) snapshot.getValue()).intValue();
                ref.setValue(ridValue+1);
                value = RECIPE_PREFIX + ridValue;

                Log.d("firebase", "Firebase rId updated to " + (ridValue+1));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });

        // TODO: 2/1/21 This function ends before onDataChange gets result, leading to null value
        return "003";
//        return value;
    }



}


