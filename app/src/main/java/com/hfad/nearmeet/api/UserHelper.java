package com.hfad.nearmeet.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import com.hfad.nearmeet.Model.User;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, GeoPoint localisation, String champRecherche) {
        //  Create User object
        User userToCreate = new User(uid, username, urlPicture, localisation, champRecherche);
        //  Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateLocalisation(GeoPoint localisation, String uid){
        return UserHelper.getUsersCollection().document(uid).update("localisation",localisation);
    }

    public static Task<Void> updateChampRecherche (String champRecherche, String uid){

        return UserHelper.getUsersCollection().document(uid).update("champRecherche", champRecherche);
    }
    
    public static Task<Void> update_isOnline(Boolean online, String uid){
        return UserHelper.getUsersCollection().document(uid).update("isOnline",online);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
