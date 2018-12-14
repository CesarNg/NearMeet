package com.hfad.nearmeet;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hfad.nearmeet.Model.Chat;
import com.hfad.nearmeet.Model.User;
import com.hfad.nearmeet.api.UserHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFriendsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.clickChat)
    RelativeLayout relativeLayout;
    @BindView(R.id.name_friend)
    TextView name_friend;
    @BindView(R.id.last_message)
    TextView last_message;
    Fragment fragmentFriendChat;

    //@BindView(R.id.item_date)
    //TextView textDate;


    public ListFriendsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithChat(Chat chat, String currentUserId){

        last_message.setText(chat.getLastMessage());
        String friendUID = null;

        if (currentUserId.equals(chat.getUidMember1()))
            friendUID = chat.getUidMember2();
        else if(currentUserId.equals(chat.getUidMember2()))
            friendUID = chat.getUidMember1();

        String finalFriendUID1 = friendUID;
        if(friendUID != null){

            UserHelper.getUser().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot userData : dataSnapshot.getChildren()){

                        if(finalFriendUID1.equals(userData.getKey())){

                            User user = userData.getValue(User.class);
                            name_friend.setText(user.getUsername());

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }



            String finalFriendUID = friendUID;

        relativeLayout.setOnClickListener(view -> {

            this.fragmentFriendChat = FriendChatFragment.newInstance(chat.getUID(), finalFriendUID);
            NavigationDrawerActivity.instance.startTransactionFragment(this.fragmentFriendChat);
        });
    }


}
