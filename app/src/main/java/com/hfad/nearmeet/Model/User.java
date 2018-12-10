package com.hfad.nearmeet.Model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;



import java.util.HashMap;
import java.util.Map;

public class User {

    private String uid;
    private String username;
    private Geopoint localisation;
    private Boolean isVisible;
    private Boolean isOnline;
    private String champRecherche;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture, Geopoint localisation, String champRecherche, Boolean isVisible) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.localisation =localisation ;
        this.champRecherche = champRecherche;
        this.isVisible = isVisible;
        this.isOnline = false;

    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getChampRecherche() {
        return champRecherche;
    }
    @Nullable public String  getUrlPicture() { return urlPicture; }
    public Geopoint getLocalisation() {
        return localisation;
    }
    public Boolean getIsVisible(){return isVisible;}
    public Boolean getIsOnline(){return isOnline;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
    public void setChampRecherche(String champRecherche) {
        this.champRecherche = champRecherche;
    }
    public void setLocalisation(Geopoint localisation) {
        this.localisation = localisation;
    }
    public void setIsOnlinee(Boolean isOnline) { this.isOnline = isOnline; }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username", username);
        result.put("urlPicture", urlPicture);
        result.put("localisation", localisation);
        result.put("champRecherche", champRecherche);
        result.put("isVisible", isVisible);
        result.put("isOnline",isOnline);

        return result;
    }

}