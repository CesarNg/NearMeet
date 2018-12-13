package com.hfad.nearmeet.tic_tac_toe.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.nearmeet.R;
import com.hfad.nearmeet.tic_tac_toe.model_game.User;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import static com.hfad.nearmeet.tic_tac_toe.Constants.FIREBASE_CLOUD_FUNCTIONS_BASE;
import static com.hfad.nearmeet.tic_tac_toe.Util.getCurrentUserId;


public class Adapter extends RecyclerView.Adapter<Holder> {
    private Context context;
    private List<User> users;


    public Adapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getName());

        holder.invite.setOnClickListener(v -> {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users")
                    .child(getCurrentUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User me = dataSnapshot.getValue(User.class);

                            OkHttpClient client = new OkHttpClient();

                            String to = user.getPushId();

                            Request request = new Request.Builder()
                                    .url(String
                                            .format("%s/sendNotification?to=%s&fromPushId=%s&fromId=%s&fromName=%s&type=%s",
                                                    FIREBASE_CLOUD_FUNCTIONS_BASE,
                                                    to,
                                                    me.getPushId(),
                                                    getCurrentUserId(),
                                                    me.getName(),
                                                    "invite"))
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) throws IOException {

                                }

                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}