package com.hfad.nearmeet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hfad.nearmeet.Model.User;
import com.hfad.nearmeet.api.UserHelper;


public class NavigationDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Fragment fragmentHome;
    private Fragment fragmentProfil;
    private Fragment fragmentChat;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_PROFIL = 1;
    private static final int FRAGMENT_CHAT = 2;
    private  NavigationView navigationView;
    private DrawerLayout drawer;

    private ImageView imageViewProfile;
    private TextView textUsername;
    private TextView textViewEmail;
    private View view;

    public NavigationDrawerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view =  navigationView.getHeaderView(0);
        imageViewProfile = view.findViewById(R.id.imageUser);
        textUsername = view.findViewById(R.id.username_text);
        textViewEmail = view.findViewById(R.id.email_text);

        this.showFirstFragment();

        this.updateUIWhenCreating();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            this.showFragment(FRAGMENT_HOME);
        } else if (id == R.id.nav_profil) {
            this.showFragment(FRAGMENT_PROFIL);
        } else if (id == R.id.nav_chat){
            this.showFragment(FRAGMENT_CHAT);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFriendChatActivity() {

       // Intent myItent = new Intent(this,FriendChatActivity.class);
       // startActivity(myItent);
    }

    private void showFragment(int fragmentIdentifier){
        switch (fragmentIdentifier){
            case FRAGMENT_HOME :
                this.showHomeFragment();
                break;
            case FRAGMENT_PROFIL:
                this.showProfilFragment();
                break;
            case FRAGMENT_CHAT:
                this.showChatFragment();
                break;
            default:
                break;
        }
    }

    private void showHomeFragment(){
        if (this.fragmentHome == null) this.fragmentHome = HomeFragment.newInstance(null,null);
        this.startTransactionFragment(this.fragmentHome);
    }

    private void showProfilFragment(){
        if (this.fragmentProfil == null) this.fragmentProfil = ProfilFragment.newInstance(null,null);
        this.startTransactionFragment(this.fragmentProfil);
    }

    private void showChatFragment(){
        if (this.fragmentChat == null) this.fragmentChat = FriendChatFragment.newInstance(null,null);
        this.startTransactionFragment(this.fragmentChat);
    }



    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_frame_layout, fragment).commit();
        }
    }

    private void showFirstFragment(){
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.nav_frame_layout);
        if (visibleFragment == null){

            this.showFragment(FRAGMENT_HOME);
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

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
            textViewEmail.setText(email);

            // 7 - Get additional data from Firestore ( Username)
            UserHelper.getUser(getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    textUsername.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });


        }
    }


}
