package com.dreamest.cookbookapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingFriendsFirebaseAdapter extends FirebaseRecyclerAdapter<String, PendingFriendsFirebaseAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private PendingFriendsFirebaseAdapter.ItemClickListener mClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options firebase options object
     */
    public PendingFriendsFirebaseAdapter(@NonNull FirebaseRecyclerOptions<String> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PendingFriendsFirebaseAdapter.ViewHolder holder, int position, @NonNull String model) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(model);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.pending_item_friend_TXT_displayName.setText(user.getDisplayName());
                holder.pending_item_friend_TXT_phoneNumber.setText(user.getPhoneNumber());

                FirebaseTools.downloadImage(mInflater.getContext(), user.getImagePath(), user.getUserID(), FirebaseTools.FILE_KEYS.JPG,
                        holder.pending_item_friend_IMG_profile_image, ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_loading), R.drawable.ic_no_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    @NonNull
    @Override
    public PendingFriendsFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mInflater = LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_friend_item, parent, false);
        return new PendingFriendsFirebaseAdapter.ViewHolder(view);
    }

    public void setClickListener(PendingFriendsFirebaseAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onAddClick(int position);

        void onRemoveClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView pending_item_friend_IMG_profile_image;
        private TextView pending_item_friend_TXT_displayName;
        private TextView pending_item_friend_TXT_phoneNumber;
        private ImageButton pending_item_friend_BTN_add;
        private ImageButton pending_item_friend_BTN_remove;

        ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);

            pending_item_friend_BTN_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onAddClick(getAdapterPosition());
                    }
                }
            });
            pending_item_friend_BTN_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onRemoveClick(getAdapterPosition());
                    }
                }
            });
        }

        public void findViews(View itemView) {
            pending_item_friend_IMG_profile_image = itemView.findViewById(R.id.pending_item_friend_IMG_profile_image);
            pending_item_friend_TXT_displayName = itemView.findViewById(R.id.pending_item_friend_TXT_displayName);
            pending_item_friend_TXT_phoneNumber = itemView.findViewById(R.id.pending_item_friend_TXT_phoneNumber);
            pending_item_friend_BTN_add = itemView.findViewById(R.id.pending_item_friend_BTN_add);
            pending_item_friend_BTN_remove = itemView.findViewById(R.id.pending_item_friend_BTN_remove);
        }
    }
}
