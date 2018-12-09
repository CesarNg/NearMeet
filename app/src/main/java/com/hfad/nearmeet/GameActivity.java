package com.hfad.nearmeet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;
import com.hfad.nearmeet.tic_tac_toe.Board;

import butterknife.BindView;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = "GameActivity";
    @BindView(R.id.canvas)
    Board canvas;
    private String withId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle extras = getIntent().getExtras();
        String type = extras.getString("type");
        if (type.equals("wifi")) {
            withId = extras.getString("withId");
            canvas.setWifiWith(withId);
            String gameId = extras.getString("gameId");
            canvas.setGameId(gameId);
            canvas.setMe(extras.getString("me"));

            FirebaseDatabase.getInstance().getReference().child("games")
                    .child(gameId)
                    .setValue(null);
        }
    }
}
