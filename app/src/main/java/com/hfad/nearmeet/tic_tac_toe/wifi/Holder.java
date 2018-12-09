package com.hfad.nearmeet.tic_tac_toe.wifi;

import android.app.DownloadManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.nearmeet.R;
import com.hfad.nearmeet.tic_tac_toe.model_game.User;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hfad.nearmeet.tic_tac_toe.Constants.FIREBASE_CLOUD_FUNCTIONS_BASE;
import static com.hfad.nearmeet.tic_tac_toe.Util.getCurrentUserId;

public class Holder extends RecyclerView.ViewHolder {
    @BindView(R.id.invite)
    Button invite;
    @BindView(R.id.username_item)
    TextView username;


    public Holder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

    }
}