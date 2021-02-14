package com.dreamest.cookbookapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatFirebaseAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ChatFirebaseAdapter.ItemClickListener mClickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatFirebaseAdapter(@NonNull FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatFirebaseAdapter.ViewHolder holder, int position, @NonNull ChatMessage model) {
        visualizeMessage(model, holder);
    }

    private void visualizeMessage(ChatMessage message, ChatFirebaseAdapter.ViewHolder holder) {
        holder.message_item_TXT_message.setText(message.getText());
        holder.message_item_TXT_sender.setText(message.getSenderName());

        Timestamp stamp = new Timestamp(message.getTimestamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timestamp = simpleDateFormat.format(stamp);
        holder.message_item_TXT_timestamp.setText(timestamp);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.message_item_LAY_master.getLayoutParams();
        if (message.getSenderID().equals(FirebaseAuth.getInstance().getUid())) { // i.e current user is the sender
            holder.message_item_LAY_container.setBackgroundColor(mInflater.getContext().getColor(R.color.my_message));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            holder.message_item_LAY_container.setBackgroundColor(mInflater.getContext().getColor(R.color.other_message));
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        }
        holder.message_item_LAY_master.setLayoutParams(params);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    @NonNull
    @Override
    public ChatFirebaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mInflater = LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new ChatFirebaseAdapter.ViewHolder(view);
    }

    public void setClickListener(ChatFirebaseAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


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
