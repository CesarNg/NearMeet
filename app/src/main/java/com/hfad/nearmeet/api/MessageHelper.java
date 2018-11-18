package com.hfad.nearmeet.api;

import android.hardware.usb.UsbRequest;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.hfad.nearmeet.Model.Message;
import com.hfad.nearmeet.Model.User;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    /*public static Query getMessageForChat(String chat, String uidReceiver){


        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .whereEqualTo("userSender.uid",uidReceiver)
                .orderBy("dateCreated")
                .limit(50);
    }*/



    public static Task<DocumentReference> createMessageForChat(String textMessage, String chat, User userSender, User userReceiver){

        // 1 - Create the Message object
        Message message = new Message(textMessage, userSender, userReceiver);

        // 2 - Store Message to Firestore
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }
}
