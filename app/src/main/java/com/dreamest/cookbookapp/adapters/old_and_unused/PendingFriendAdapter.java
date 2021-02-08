package com.dreamest.cookbookapp.adapters.old_and_unused;

import android.content.Context;
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
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class PendingFriendAdapter extends RecyclerView.Adapter<PendingFriendAdapter.ViewHolder> {
    private ArrayList<User> mData;
    private LayoutInflater mInflater;
    private PendingFriendAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public PendingFriendAdapter(Context context, ArrayList<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public PendingFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pending_friend_item, parent, false);
        return new PendingFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingFriendAdapter.ViewHolder holder, int position) {
        User user = mData.get(position);
        holder.pending_item_friend_TXT_displayName.setText(user.getDisplayName());
        holder.pending_item_friend_TXT_phoneNumber.setText(user.getPhoneNumber());

        FirebaseTools.downloadImage(mInflater.getContext(), user.getImagePath(), user.getUserID(), UtilityPack.FILE_KEYS.JPG,
                holder.pending_item_friend_IMG_profile_image, ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_loading), R.drawable.ic_no_image);
        }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    User getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(PendingFriendAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onAddClick(int position);
        void onRemoveClick(int position);
    }

    // stores and recycles views as they are scrolled off screen
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
                    if(mClickListener != null) {
                        mClickListener.onAddClick(getAdapterPosition());
                    }
                }
            });
            pending_item_friend_BTN_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mClickListener != null) {
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
