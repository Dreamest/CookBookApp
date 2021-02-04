package com.dreamest.cookbookapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingRecipeAdapter extends RecyclerView.Adapter<PendingRecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> mData;
    private LayoutInflater mInflater;
    private PendingRecipeAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public PendingRecipeAdapter(Context context, ArrayList<Recipe> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public PendingRecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pending_recipe_item, parent, false);
        return new PendingRecipeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = mData.get(position);
        holder.pending_item_recipe_TXT_title.setText(recipe.getTitle());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(recipe.getOwnerID()).child(UtilityPack.KEYS.DISPLAY_NAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.pending_item_recipe_TXT_owner.setText(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    Recipe getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(PendingRecipeAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onAddClick(int position);
        void onRemoveClick(int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pending_item_recipe_TXT_title;
        private TextView pending_item_recipe_TXT_owner;
        private ImageButton pending_item_recipe_BTN_add;
        private ImageButton pending_item_recipe_BTN_remove;

        ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);

            pending_item_recipe_BTN_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mClickListener != null) {
                        mClickListener.onAddClick(getAdapterPosition());
                    }
                }
            });
            pending_item_recipe_BTN_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mClickListener != null) {
                        mClickListener.onRemoveClick(getAdapterPosition());
                    }
                }
            });
        }

        public void findViews(View itemView) {
            pending_item_recipe_TXT_title = itemView.findViewById(R.id.pending_item_recipe_TXT_title);
            pending_item_recipe_TXT_owner = itemView.findViewById(R.id.pending_item_recipe_TXT_owner);
            pending_item_recipe_BTN_add = itemView.findViewById(R.id.pending_item_recipe_BTN_add);
            pending_item_recipe_BTN_remove = itemView.findViewById(R.id.pending_item_recipe_BTN_remove);

        }
    }
}
