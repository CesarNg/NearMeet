package com.hfad.nearmeet.Model;

public class Chat {

    private String UID;
    private String uidMember1;
    private String uidMember2;
    private String lastMessage;

    public Chat(){

    }
    public Chat(String UID, String uidMember1, String uidMember2){
        this.UID = UID;
        this.uidMember1 = uidMember1;
        this.uidMember2 = uidMember2;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUidMember1() {
        return uidMember1;
    }

    public void setUidMember1(String uidMember1) {
        this.uidMember1 = uidMember1;
    }

    public String getUidMember2() {
        return uidMember2;
    }

    public void setUidMember2(String uidMember2) {
        this.uidMember2 = uidMember2;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
