package model;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;

public class User {

    private String uid;
    private String username;
    private GeoPoint localisation;
    private Boolean isMentor;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture, GeoPoint localisation) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.localisation =localisation ;
        this.isMentor = false;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @Nullable public String  getUrlPicture() { return urlPicture; }
    public GeoPoint getLocalisation() {
        return localisation;
    }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public void setLocalisation(GeoPoint localisation) {
        this.localisation = localisation;
    }
}