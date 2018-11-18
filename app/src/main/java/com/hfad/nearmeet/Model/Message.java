package com.hfad.nearmeet.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private User userSender;
    private String urlImage;
    private User userReceiver;

    public Message() { }

    public Message(String message, User userSender, User userReceiver) {
        this.message = message;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
    }

    public Message(String message, String urlImage, User userSender) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }

    public User getUserReceiver() {
        return userReceiver;
    }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(User userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
    }
}
