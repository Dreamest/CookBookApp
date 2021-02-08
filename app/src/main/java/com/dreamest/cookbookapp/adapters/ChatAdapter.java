package com.dreamest.cookbookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.ChatMessage;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String chatID;

    // data is passed into the constructor
    public ChatAdapter(Context context, ArrayList<String> data, String chatID) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.chatID = chatID;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = mData.get(position);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(UtilityPack.KEYS.CHATS).child(chatID).child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                visualizeMessage(message, holder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void visualizeMessage(ChatMessage message, ViewHolder holder) {
        holder.message_item_TXT_message.setText(message.getText());
        holder.message_item_TXT_sender.setText(message.getSenderName());

        Timestamp stamp = new Timestamp(message.getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timestamp = simpleDateFormat.format(stamp);
        holder.message_item_TXT_timestamp.setText(timestamp);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.message_item_LAY_master.getLayoutParams();
        if(message.getSenderID().equals(FirebaseAuth.getInstance().getUid())) { // i.e current user is the sender
            holder.message_item_LAY_container.setBackgroundColor(mInflater.getContext().getColor(R.color.my_message));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            holder.message_item_LAY_container.setBackgroundColor(mInflater.getContext().getColor(R.color.other_message));
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }
        holder.message_item_LAY_master.setLayoutParams(params);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
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
        private RelativeLayout message_item_LAY_master; //for positioning
        private RelativeLayout message_item_LAY_container; //for color
        private TextView message_item_TXT_sender;
        private TextView message_item_TXT_message;
        private TextView message_item_TXT_timestamp;



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
            message_item_TXT_sender = itemView.findViewById(R.id.message_item_TXT_sender);
            message_item_TXT_message = itemView.findViewById(R.id.message_item_TXT_message);
            message_item_TXT_timestamp = itemView.findViewById(R.id.message_item_TXT_timestamp);
            message_item_LAY_master = itemView.findViewById(R.id.message_item_LAY_master);
            message_item_LAY_container = itemView.findViewById(R.id.message_item_LAY_container);
        }
    }
}