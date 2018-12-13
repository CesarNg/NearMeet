package com.hfad.nearmeet.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.firestore.GeoPoint;


import com.hfad.nearmeet.Model.Geopoint;
import com.hfad.nearmeet.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static DatabaseReference getDatabaseRef(){
        return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
    }

    // --- CREATE ---

   /* public static Task<Void> createUser(String uid, String username, String urlPicture, GeoPoint localisation, String champRecherche, Boolean isVisible) {
        //  Create User object
        User userToCreate = new User(uid, username, urlPicture, localisation, champRecherche, isVisible);
        //  Add a new User Document to Firestore
        return UserHelper.getDatabaseRef()
                .child(COLLECTION_NAME)
                .child(uid)
                .setValue(userToCreate);
               /* .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document*/


    public static Task<Void> createUser(String uid, String username, String urlPicture, Geopoint localisation, String champRecherche, Boolean isVisible) {


        //  Create User object
        User userToCreate = new User(uid, username, urlPicture, localisation, champRecherche, isVisible);
        return UserHelper.getDatabaseRef()
                .child(uid)
                .setValue(userToCreate);
               /* .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document*/
    }

    /*public void  createUser(String uid, String username, String urlPicture, GeoPoint localisation, String champRecherche, Boolean isVisible) {

        String key = getDatabaseRef().child(COLLECTION_NAME).push().getKey();
        //  Create User object
        User userToCreate = new User(uid, username, urlPicture, localisation, champRecherche, isVisible);
        Map<String, Object> userValues = userToCreate.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + key, userValues);
        //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        getDatabaseRef().updateChildren(childUpdates);
    }*/

    // --- GET ---

   /* public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getDatabaseRef()
                .child(COLLECTION_NAME)
                .child(uid)
                .get.document(uid).get();
    }*/



    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        //return UserHelper.getUsersCollection().document(uid).update("username", username);
        return UserHelper.getDatabaseRef().child(uid).child("username").setValue(username);
    }
    public static Task<Void> updateLocalisation(GeoPoint localisation, String uid){
        return UserHelper.getDatabaseRef().child(uid).child("localisation").setValue(localisation);
    }
    public static Task<Void> updateChampRecherche (String champRecherche, String uid){
        return UserHelper.getDatabaseRef().child(uid).child("champRecherche").setValue(champRecherche);
    }
    public static Task<Void> updateIsOnline (Boolean isOnline, String uid){
        return UserHelper.getDatabaseRef().child(uid).child("isOnline").setValue(isOnline);
    }

    public static Task<Void> updateIsVisible (Boolean isVisibe, String uid){
        return UserHelper.getDatabaseRef().child(uid).child("isVisible").setValue(isVisibe);
    }
    public static Task<Void> updateInterets (ArrayList<String> interets, String uid){
        return UserHelper.getDatabaseRef().child(uid).child("interets").setValue(interets.subList(0,interets.size()));
    }


    // Query

    public static Query getUser(){
        return UserHelper.getDatabaseRef();
    }


}
