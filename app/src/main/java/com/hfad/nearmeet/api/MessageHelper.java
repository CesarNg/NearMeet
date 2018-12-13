package com.hfad.nearmeet.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.hfad.nearmeet.Model.Message;
import com.hfad.nearmeet.Model.User;

import java.util.Date;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static DatabaseReference getDatabaseRef(){
        return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
    }

    public static Query getAllMessageForChat(){
        return MessageHelper.getDatabaseRef().orderByChild("uid");
    }




    public static Task<Void> createMessageForChat(String textMessage, String uidReceiver, String uidSender){

        // 1 - Create the Message object

        String key = MessageHelper.getDatabaseRef().push().getKey();
        Message message = new Message(textMessage, uidSender, uidReceiver,key,null);

        return MessageHelper.getDatabaseRef().child(key).setValue(message);

    }
}
