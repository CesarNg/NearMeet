package com.hfad.nearmeet.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FriendsHelper {

    private static final String COLLECTION_NAME = "friends";

    public static DatabaseReference getDatabaseRef(){
        return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
    }

    public static Task<Void> createFriend(String chatUID, String userUID){

        return getDatabaseRef().child(userUID).child(chatUID).setValue(null);
    }

    public static Query getFriend(){
        return FriendsHelper.getDatabaseRef();
    }

}
