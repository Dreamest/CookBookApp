package com.dreamest.cookbookapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.activities.AddFriendActivity;
import com.dreamest.cookbookapp.activities.ChatActivity;
import com.dreamest.cookbookapp.activities.PendingFriendsActivity;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.adapters.FriendFirebaseAdapter;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.google.android.material.button.MaterialButton;

public class FriendslistFragment extends Fragment {
    private RecyclerView friendslist_LST_friends;
    private ImageButton friendslist_BTN_add_friend;
    private RelativeLayout friendslist_LAY_master;
    private MaterialButton friendslist_BTN_pending;
    private TextView friendslist_TXT_no_friends;
    private ImageView friendslist_IMG_background;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        findViews(view);
        initViews();
        bindAdapter();
        observePendingFriends();
        observeCurrentFriends();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleCurrentFriendsEntry();
        handlePendingRequestsEntry();
    }

    private void handlePendingRequestsEntry() {
        int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
        if (pendingSize > 0) {
            String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
            friendslist_BTN_pending.setText(message);
            friendslist_BTN_pending.setVisibility(View.VISIBLE);
        } else {
            friendslist_BTN_pending.setVisibility(View.GONE);
        }
    }

    private void handleCurrentFriendsEntry() {
        int friendslistSize = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItemCount();
        if (friendslistSize == 0) {
            friendslist_TXT_no_friends.setVisibility(View.VISIBLE);
        } else {
            friendslist_TXT_no_friends.setVisibility(View.GONE);
        }
    }

    private void findViews(View view) {
        friendslist_LST_friends = view.findViewById(R.id.friendslist_LST_friends);
        friendslist_BTN_add_friend = view.findViewById(R.id.friendslist_BTN_add_friend);
        friendslist_LAY_master = view.findViewById(R.id.friendslist_LAY_master);
        friendslist_BTN_pending = view.findViewById(R.id.friendslist_BTN_pending);
        friendslist_TXT_no_friends = view.findViewById(R.id.friendslist_TXT_no_friends);
        friendslist_IMG_background = view.findViewById(R.id.friendslist_IMG_background);
    }

    private void observeCurrentFriends() {
        FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                friendslist_TXT_no_friends.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int friendslistSize = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItemCount();
                if (friendslistSize == 0) {
                    friendslist_TXT_no_friends.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void bindAdapter() {
        friendslist_LST_friends.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().setClickListener(new FriendFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openChatWith(FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItem(position));
            }
        });
        friendslist_LST_friends.setAdapter(FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter());
    }

    private void observePendingFriends() {

        FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
                String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
                friendslist_BTN_pending.setText(message);
                friendslist_BTN_pending.setVisibility(View.VISIBLE);

                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
                if (pendingSize == 0) {
                    friendslist_BTN_pending.setVisibility(View.GONE);
                } else {
                    String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
                    friendslist_BTN_pending.setText(message);
                    friendslist_BTN_pending.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void startAddFriend() {
        Intent myIntent = new Intent(getActivity(), AddFriendActivity.class);
        startActivity(myIntent);
    }

    private void initViews() {
        friendslist_BTN_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddFriend();
            }
        });

        friendslist_BTN_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPendingFriends();
            }
        });
    }
    private void openPendingFriends() {
        Intent myIntent = new Intent(getActivity(), PendingFriendsActivity.class);
        startActivity(myIntent);
    }

    private void openChatWith(String friendID) {
        MySharedPreferences.getMsp().putString(MySharedPreferences.KEYS.USER_ID, friendID);
        Intent myIntent = new Intent(getActivity(), ChatActivity.class);
        startActivity(myIntent);
    }


}
