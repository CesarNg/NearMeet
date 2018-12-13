package com.hfad.nearmeet.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private String uidSender;
    private String uidReceiver;
    private String uid;

    public Message() { }

    public Message(String message, String uidSender, String uidReceiver, String uid, Date dateCreated) {
        this.message = message;
        this.uidSender = uidSender;
        this.uidReceiver = uidReceiver;
        this.uid = uid;
        this.dateCreated = dateCreated;
    }


    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public String getUidReceiver() {
        return uidReceiver;
    }

    public String getUidSender() {
        return uidSender;
    }

    public String getUid() {
        return uid;
    }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    public void setUidReceiver(String uidReceiver) {
        this.uidReceiver = uidReceiver;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
