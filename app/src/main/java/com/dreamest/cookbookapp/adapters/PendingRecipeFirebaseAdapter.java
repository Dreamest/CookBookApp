package com.dreamest.cookbookapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingRecipeFirebaseAdapter extends FirebaseRecyclerAdapter<String, PendingRecipeFirebaseAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private PendingRecipeFirebaseAdapter.ItemClickListener mClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PendingRecipeFirebaseAdapter(@NonNull FirebaseRecyclerOptions<String> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PendingRecipeFirebaseAdapter.ViewHolder holder, int position, @NonNull String model) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.RECIPES)
                .child(model);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                holder.pending_item_recipe_TXT_title.setText(recipe.getTitle());
/*
        Calling the FirebaseDatabase from here ensures that the user's DisplayName is up to date as we don't actually store it in recipe
        This is intentional because if we wanted to update a user's name in all his recipes otherwise, we would have to iterate over all recipes and fix manually in every one.
        Which might be smarter for a small database with a few users and a few recipes, but it's a good practice to think about the future.
 */
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(FirebaseTools.DATABASE_KEYS.USERS).child(recipe.getOwnerID()).child(FirebaseTools.DATABASE_KEYS.DISPLAY_NAME);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.pending_item_recipe_TXT_owner.setText(snapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public PendingRecipeFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mInflater = LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_recipe_item, parent, false);
        return new PendingRecipeFirebaseAdapter.ViewHolder(view);
    }
    // allows clicks events to be caught

    public void setClickListener(PendingRecipeFirebaseAdapter.ItemClickListener itemClickListener) {
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
