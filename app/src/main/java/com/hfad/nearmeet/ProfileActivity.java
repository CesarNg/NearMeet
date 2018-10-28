package com.hfad.nearmeet;

import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

public class ProfileActivity extends BaseActivity implements View.OnClickListener{



    ImageView imageViewProfile;
    TextInputEditText textInputEditTextUsername;
    TextView textViewEmail;
    ProgressBar progressBar;
    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageViewProfile = findViewById(R.id.profile_activity_imageview_profile);
        textInputEditTextUsername = findViewById(R.id.profile_activity_edit_text_username);
        textViewEmail = findViewById(R.id.profile_activity_text_view_email);
        progressBar = findViewById(R.id.profile_activity_progress_bar);

        findViewById(R.id.profile_activity_button_sign_out).setOnClickListener(this);
        findViewById(R.id.profile_activity_button_update).setOnClickListener(this);
        findViewById(R.id.profile_activity_button_delete).setOnClickListener(this);

        this.updateUIWhenCreating();
    }



    // --------------------
    // UI
    // --------------------

    // 1 - Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            this.textInputEditTextUsername.setText(username);
            this.textViewEmail.setText(email);
        }
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 1 - Create http requests (SignOut & Delete)

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    // --------------------
    // UI
    // --------------------

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<? super Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.profile_activity_button_sign_out) {
            this.signOutUserFromFirebase();
        } else if (i == R.id.profile_activity_button_delete) {
            this.deleteUserFromFirebase();
        } else if (i == R.id.profile_activity_button_update) {
            //Todo
        }
    }
}

