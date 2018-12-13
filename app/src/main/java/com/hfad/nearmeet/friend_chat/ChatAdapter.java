package com.hfad.nearmeet.friend_chat;
import android.app.DownloadManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hfad.nearmeet.Model.Message;
import com.hfad.nearmeet.R;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder>{
    //FOR DATA
    // private final RequestManager glide;
    private final String idCurrentUser;
    private final List<Message> list;
    //FOR COMMUNICATION
    private Listener callback;
    public interface Listener {
        void onDataChanged();
    }
    public ChatAdapter(List<Message> list, String idCurrentUser) {
        //this.glide = glide;
        // this.callback = callback;
        this.idCurrentUser = idCurrentUser;
        this.list = list;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friend_chat_item, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        if(list.size() != 0)
            messageViewHolder.updateWithMessage(list.get(i),idCurrentUser);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}