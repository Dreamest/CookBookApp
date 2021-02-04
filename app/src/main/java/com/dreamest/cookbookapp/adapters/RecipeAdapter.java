package com.dreamest.cookbookapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private ArrayList<Recipe> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecipeAdapter(Context context, ArrayList<Recipe> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipe_list_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = mData.get(position);
        holder.listRecipe_TXT_title.setText(recipe.getTitle());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(recipe.getOwnerID()).child(UtilityPack.KEYS.DISPLAY_NAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.listRecipe_TXT_owner.setText(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
        holder.listRecipe_TXT_date.setText(recipe.getDate());
        Glide
                .with(mInflater.getContext())
                .load(recipe.getImage())
                .centerCrop()
                .into(holder.listRecipe_IMG_image)
                .onLoadStarted(ContextCompat.getDrawable(holder.listRecipe_IMG_image.getContext(), R.drawable.ic_no_image));
        holder.listRecipe_TXT_time.setText(recipe.getPrepTime() + "");

        for (int i = 0; i < recipe.getDifficulty(); i++) {
            holder.stars[i].setImageResource(R.drawable.ic_full_star);
        }
        for (int i = recipe.getDifficulty(); i < 5; i++) {
            holder.stars[i].setImageResource(R.drawable.ic_empty_star);
        }

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
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView listRecipe_TXT_title;
        TextView listRecipe_TXT_date;
        TextView listRecipe_TXT_owner;
        ImageView listRecipe_IMG_image;
        TextView listRecipe_TXT_time;
        ImageView[] stars;


        ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        Log.d("dddd", "inside recipeAdapter " + getAdapterPosition());
                        mClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        public void findViews(View itemView) {
            stars = new ImageView[5];
            listRecipe_TXT_title = itemView.findViewById(R.id.listRecipe_TXT_title);
            listRecipe_TXT_date = itemView.findViewById(R.id.listRecipe_TXT_date);
            listRecipe_TXT_owner = itemView.findViewById(R.id.listRecipe_TXT_owner);
            listRecipe_IMG_image = itemView.findViewById(R.id.listRecipe_IMG_image);
            listRecipe_TXT_time = itemView.findViewById(R.id.listRecipe_TXT_time);
            stars[0] = itemView.findViewById(R.id.listRecipe_IMG_star1);
            stars[1] = itemView.findViewById(R.id.listRecipe_IMG_star2);
            stars[2] = itemView.findViewById(R.id.listRecipe_IMG_star3);
            stars[3] = itemView.findViewById(R.id.listRecipe_IMG_star4);
            stars[4] = itemView.findViewById(R.id.listRecipe_IMG_star5);
        }
    }
}