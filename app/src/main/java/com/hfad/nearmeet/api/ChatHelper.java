package com.hfad.nearmeet.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    // --- COLLECTION REFERENCE ---


    public static DatabaseReference getDatabaseRef(){
        return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
    }
}
