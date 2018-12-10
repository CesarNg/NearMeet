package com.hfad.nearmeet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.hfad.nearmeet.Model.Geopoint;
import com.hfad.nearmeet.api.UserHelper;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText signUpFieldUsername,signUpFieldPassword,signUpFieldConfirmPassword,signUpFieldEmail;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // View
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        signUpFieldUsername = findViewById(R.id.SignUpFieldUsername);
        signUpFieldPassword = findViewById(R.id.SignUpFieldPassword);
        signUpFieldConfirmPassword = findViewById(R.id.SignUpFieldConfirmPassword);
        signUpFieldEmail = findViewById(R.id.SignUpFieldEmail);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.viewSignInForm).setOnClickListener(this);
        findViewById(R.id.signUpButton).setOnClickListener(this);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebookLoginButton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startProfileActivity();
        }
    }

    private void createAccount(String email, String password, final String username) {
        Log.d(TAG, "createAccount:" + email);
        if (validateSignUpForm()) {
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUserInFirestore(username);
                            user = mAuth.getCurrentUser();
                            startProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateSignInForm()) {
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateSignInForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(this.getString(R.string.field_required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(this.getString(R.string.field_required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    private boolean validateSignUpForm() {
        boolean valid = true;
        String email = signUpFieldEmail.getText().toString();
        String password = signUpFieldPassword.getText().toString();
        String confirmPassword = signUpFieldConfirmPassword.getText().toString();
        String username = signUpFieldUsername.getText().toString();

        // Email field
        if (TextUtils.isEmpty(email)) {
            signUpFieldEmail.setError(this.getString(R.string.field_required));
            valid = false;
        } else {
            signUpFieldEmail.setError(null);
        }
        // Password field
        if (TextUtils.isEmpty(password)) {
            signUpFieldPassword.setError(this.getString(R.string.field_required));
            valid = false;
        } else {
            signUpFieldPassword.setError(null);
        }
        // Confirm password field
        if (TextUtils.isEmpty(confirmPassword)) {
            signUpFieldConfirmPassword.setError(this.getString(R.string.field_required));
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            signUpFieldConfirmPassword.setError(this.getString(R.string.password_confirm_invalid));
            valid = false;
        } else {
            signUpFieldConfirmPassword.setError(null);
        }

        // Username field
        if (TextUtils.isEmpty(username)) {
            signUpFieldUsername.setError(this.getString(R.string.field_required));
            valid = false;
        } else {
            signUpFieldUsername.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (viewId == R.id.viewSignInForm){
            showSigninForm();
        }else if (viewId == R.id.signUpButton){
            showSignUpForm();
        }else if(viewId == R.id.emailCreateAccountButton){
            createAccount(  signUpFieldEmail.getText().toString(),
                    signUpFieldPassword.getText().toString(),
                    signUpFieldUsername.getText().toString());
        }

    }

    private void showSignUpForm() {
        findViewById(R.id.SignInForm).setVisibility(View.GONE);
        findViewById(R.id.emailSignUpForm).setVisibility(View.VISIBLE);
    }

    private void showSigninForm() {
        findViewById(R.id.SignInForm).setVisibility(View.VISIBLE);
        findViewById(R.id.emailSignUpForm).setVisibility(View.GONE);
    }

    private void startProfileActivity(){
        UserHelper.updateIsOnline(true, getCurrentUser().getUid());
        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        startActivity(intent);
    }

    private void createUserInFirestore(String username){
        if (this.getCurrentUser() != null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String uid = this.getCurrentUser().getUid();
            Geopoint localisation = null;
            String champRecherche = "100 M";
            Boolean isVisible = false;

            UserHelper.createUser(uid, username, urlPicture, localisation, champRecherche, isVisible).addOnFailureListener(this.onFailureListener());

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startProfileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}