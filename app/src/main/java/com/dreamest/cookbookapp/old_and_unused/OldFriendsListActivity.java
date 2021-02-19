//package com.dreamest.cookbookapp.old_and_unused;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.dreamest.cookbookapp.R;
//import com.dreamest.cookbookapp.activities.AddFriendActivity;
//import com.dreamest.cookbookapp.activities.BaseActivity;
//import com.dreamest.cookbookapp.activities.ChatActivity;
//import com.dreamest.cookbookapp.activities.PendingFriendsActivity;
//import com.dreamest.cookbookapp.adapters.FriendFirebaseAdapter;
//import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
//import com.dreamest.cookbookapp.utility.MySharedPreferences;
//import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
//import com.google.android.material.button.MaterialButton;
//
//public class OldFriendsListActivity extends BaseActivity {
//    private RecyclerView friendslist_LST_friends;
//    private ImageButton friendslist_BTN_add_friend;
//    private RelativeLayout friendslist_LAY_master;
//    private MaterialButton friendslist_BTN_pending;
//    private TextView friendslist_TXT_no_friends;
//    private ImageView friendslist_IMG_background;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_friends_list);
//
//        findViews();
//        initViews();
//        bindAdapter();
//        observePendingFriends();
//        observeCurrentFriends();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        handleCurrentFriendsEntry();
//        handlePendingRequestsEntry();
//    }
//
//    private void observeCurrentFriends() {
//        FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                friendslist_TXT_no_friends.setVisibility(View.GONE);
//                super.onItemRangeInserted(positionStart, itemCount);
//            }
//
//            @Override
//            public void onItemRangeRemoved(int positionStart, int itemCount) {
//                int friendslistSize = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItemCount();
//                if (friendslistSize == 0) {
//                    friendslist_TXT_no_friends.setVisibility(View.VISIBLE);
//                }
//                super.onItemRangeRemoved(positionStart, itemCount);
//            }
//        });
//    }
//
//    private void handleCurrentFriendsEntry() {
//        int friendslistSize = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItemCount();
//        if (friendslistSize == 0) {
//            friendslist_TXT_no_friends.setVisibility(View.VISIBLE);
//        } else {
//            friendslist_TXT_no_friends.setVisibility(View.GONE);
//        }
//    }
//
//    private void bindAdapter() {
//        friendslist_LST_friends.setLayoutManager(new LinearLayoutManager(this));
//
//        FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().setClickListener(new FriendFirebaseAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                openChatWith(FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItem(position));
//            }
//        });
//        friendslist_LST_friends.setAdapter(FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter());
//    }
//
//    private void observePendingFriends() {
//
//        FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
//                String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
//                friendslist_BTN_pending.setText(message);
//                friendslist_BTN_pending.setVisibility(View.VISIBLE);
//
//                super.onItemRangeInserted(positionStart, itemCount);
//            }
//
//            @Override
//            public void onItemRangeRemoved(int positionStart, int itemCount) {
//                int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
//                if (pendingSize == 0) {
//                    friendslist_BTN_pending.setVisibility(View.GONE);
//                } else {
//                    String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
//                    friendslist_BTN_pending.setText(message);
//                    friendslist_BTN_pending.setVisibility(View.VISIBLE);
//                }
//                super.onItemRangeRemoved(positionStart, itemCount);
//            }
//        });
//    }
//
//    private void handlePendingRequestsEntry() {
//        int pendingSize = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
//        if (pendingSize > 0) {
//            String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
//            friendslist_BTN_pending.setText(message);
//            friendslist_BTN_pending.setVisibility(View.VISIBLE);
//        } else {
//            friendslist_BTN_pending.setVisibility(View.GONE);
//        }
//    }
//
//    private void startAddFriend() {
//        Intent myIntent = new Intent(OldFriendsListActivity.this, AddFriendActivity.class);
//        startActivity(myIntent);
//    }
//
//    private void initViews() {
//        Glide
//                .with(this)
//                .load(R.drawable.background_simple_waves)
//                .into(friendslist_IMG_background);
//        friendslist_BTN_add_friend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAddFriend();
//            }
//        });
//        friendslist_LST_friends.setOnTouchListener(new OnSwipeTouchListener(this) {
//            public void onSwipeLeft() {
//                finish();
//            }
//
//        });
//
//        friendslist_LAY_master.setOnTouchListener(new OnSwipeTouchListener(this) {
//            public void onSwipeLeft() {
//                finish();
//            }
//        });
//
//        friendslist_BTN_pending.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pendingFriends();
//            }
//        });
//    }
//
//    private void pendingFriends() {
//        Intent myIntent = new Intent(this, PendingFriendsActivity.class);
//        startActivity(myIntent);
//    }
//
//
//    private void findViews() {
//        friendslist_LST_friends = findViewById(R.id.friendslist_LST_friends);
//        friendslist_BTN_add_friend = findViewById(R.id.friendslist_BTN_add_friend);
//        friendslist_LAY_master = findViewById(R.id.friendslist_LAY_master);
//        friendslist_BTN_pending = findViewById(R.id.friendslist_BTN_pending);
//        friendslist_TXT_no_friends = findViewById(R.id.friendslist_TXT_no_friends);
//        friendslist_IMG_background = findViewById(R.id.friendslist_IMG_background);
//    }
//
//
//    private void openChatWith(String friendID) {
//        MySharedPreferences.getMsp().putString(MySharedPreferences.KEYS.USER_ID, friendID);
//        Intent myIntent = new Intent(this, ChatActivity.class);
//        startActivity(myIntent);
//    }
//}