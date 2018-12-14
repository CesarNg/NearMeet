package com.hfad.nearmeet.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hfad.nearmeet.Model.Chat;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    // --- COLLECTION REFERENCE ---


    public static DatabaseReference getDatabaseRef(){
        return FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
    }

    public static Task<Void> createChat(String uidReceiver, String uidSender){

        // 1 - Create the Message object

        String key = getDatabaseRef().push().getKey();
        Chat chat = new Chat(key,uidReceiver,uidSender);

        FriendsHelper.createFriend(key,uidSender);
        FriendsHelper.createFriend(key,uidReceiver);

         return ChatHelper.getDatabaseRef().child(key).child("members").setValue(chat);

    }

    public static Task<Void> updateLastMessage(String text, String ChatUID){

        return ChatHelper.getDatabaseRef().child(ChatUID).child("members").child("lastMessage").setValue(text);
    }

    public static Query getCHat(){

        return ChatHelper.getDatabaseRef();
    }
}
