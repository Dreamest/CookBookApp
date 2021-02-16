package com.dreamest.cookbookapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeFirebaseAdapter extends FirebaseRecyclerAdapter<String, RecipeFirebaseAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private RecipeFirebaseAdapter.ItemClickListener mClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options firebase options object
     */
    public RecipeFirebaseAdapter(@NonNull FirebaseRecyclerOptions<String> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipeFirebaseAdapter.ViewHolder holder, int position, @NonNull String model) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.RECIPES)
                .child(model);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                holder.listRecipe_TXT_title.setText(recipe.getTitle());

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
                        holder.listRecipe_TXT_owner.setText(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
                    }
                });
                holder.listRecipe_TXT_date.setText(recipe.getDate());
                FirebaseTools.downloadImage(mInflater.getContext(), recipe.getImagePath(), recipe.getRecipeID(), FirebaseTools.FILE_KEYS.JPG, holder.listRecipe_IMG_image,
                        ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_loading), R.drawable.ic_no_image);
                holder.listRecipe_TXT_time.setText(recipe.getPrepTime() + "");

                for (int i = 0; i < recipe.getDifficulty(); i++) {
                    holder.stars[i].setImageResource(R.drawable.ic_full_star);
                }
                for (int i = recipe.getDifficulty(); i < 5; i++) {
                    holder.stars[i].setImageResource(R.drawable.ic_empty_star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    @NonNull
    @Override
    public RecipeFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mInflater = LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeFirebaseAdapter.ViewHolder(view);
    }

    public void setClickListener(RecipeFirebaseAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView listRecipe_TXT_title;
        private TextView listRecipe_TXT_date;
        private TextView listRecipe_TXT_owner;
        private ImageView listRecipe_IMG_image;
        private TextView listRecipe_TXT_time;
        private ImageView[] stars;


        ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
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