package com.hfad.nearmeet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hfad.nearmeet.Model.Message;
import com.hfad.nearmeet.Model.User;
import com.hfad.nearmeet.api.MessageHelper;
import com.hfad.nearmeet.api.UserHelper;
import com.hfad.nearmeet.friend_chat.FriendChatAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendChatActivity extends BaseActivity implements FriendChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_friend_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_friend_chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_friend_chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.activity_friend_chat_image_chosen_preview)
    ImageView imageViewPreview;

    // FOR DATA
    // 2 - Declaring Adapter and data
    private FriendChatAdapter FriendChatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String currentChatName;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);
        ButterKnife.bind(this);

        this.configureRecyclerView(CHAT_NAME_ANDROID);
       // this.configureToolbar();
        this.getCurrentUserFromFirestore();
    }


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.activity_friend_chat_send_button)
    public void onClickSendMessage() {

        // 1 - Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
            // 2 - Create a new Message to Firestore
            MessageHelper.createMessageForChat(editTextMessage.getText().toString(), this.currentChatName, modelCurrentUser).addOnFailureListener(this.onFailureListener());
            // 3 - Reset text field
            this.editTextMessage.setText("");

        }
    }

    @OnClick({ R.id.activity_friend_chat_android_chat_button, R.id.activity_friend_chat_firebase_chat_button, R.id.activity_friend_chat_bug_chat_button})
    public void onClickChatButtons(ImageButton imageButton) {
        // 8 - Re-Configure the RecyclerView depending chosen chat
        switch (Integer.valueOf(imageButton.getTag().toString())){
            case 10:
                this.configureRecyclerView(CHAT_NAME_ANDROID);
                break;
            case 20:
                this.configureRecyclerView(CHAT_NAME_FIREBASE);
                break;
            case 30:
                this.configureRecyclerView(CHAT_NAME_BUG);
                break;
        }
    }

    @OnClick(R.id.activity_friend_chat_add_file_button)
    public void onClickAddFile() { }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.FriendChatAdapter = new FriendChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid());
        FriendChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(FriendChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.FriendChatAdapter);
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.FriendChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }


}