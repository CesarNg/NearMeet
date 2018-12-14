package com.hfad.nearmeet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hfad.nearmeet.Model.Chat;
import com.hfad.nearmeet.friend_chat.MessageViewHolder;

import java.util.List;

public class ListFriendsAdapter extends RecyclerView.Adapter<ListFriendsHolder> {

    private final String idCurrentUser;
    private final List<Chat> list;


    public ListFriendsAdapter(List<Chat> list, String idCurrentUser){
        this.list = list;
        this.idCurrentUser = idCurrentUser;
    }

    @NonNull
    @Override
    public ListFriendsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ListFriendsHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_friends_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ListFriendsHolder listFriendsHolder, int i) {

        if(list.size() != 0)
            listFriendsHolder.updateWithChat(list.get(i),idCurrentUser);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
