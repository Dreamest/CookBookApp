package com.dreamest.cookbookapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FriendFirebaseAdapter extends FirebaseRecyclerAdapter<String, FriendFirebaseAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private FriendFirebaseAdapter.ItemClickListener mClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options firebase options object
     */
    public FriendFirebaseAdapter(@NonNull FirebaseRecyclerOptions<String> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendFirebaseAdapter.ViewHolder holder, int position, @NonNull String model) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child(model).getValue(User.class);
                holder.friend_TXT_name.setText(user.getDisplayName());
                FirebaseTools.downloadImage(mInflater.getContext(), user.getImagePath(), user.getUserID(), FirebaseTools.FILE_KEYS.JPG,
                        holder.friend_IMG_profile, ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_loading), R.drawable.ic_man_avatar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    @NonNull
    @Override
    public FriendFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mInflater = LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item, parent, false);
        return new FriendFirebaseAdapter.ViewHolder(view);
    }

    public void setClickListener(FriendFirebaseAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView friend_IMG_profile;
        private TextView friend_TXT_name;


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
            friend_TXT_name = itemView.findViewById(R.id.friend_TXT_name);
            friend_IMG_profile = itemView.findViewById(R.id.friend_IMG_profile);

        }
    }
}
