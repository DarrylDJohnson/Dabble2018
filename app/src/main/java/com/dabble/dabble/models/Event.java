package com.dabble.dabble.models;

import java.util.ArrayList;

public class Event {

    public Event(){}

    public Event(String uid, String oid, String title, String photoUrl, long date, ArrayList<String> guests){
        this.uid = uid;
        this.oid = oid;
        this.title = title;
        this.photoUrl = photoUrl;
        this.date = date;
        this.guests = guests;
    }

    private String oid;
    private String uid;
    private String title;
    private String photoUrl;
    private long date;
    private ArrayList<String> guests;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getGuests() {
        return guests;
    }

    public void setGuests(ArrayList<String> guests) {
        this.guests = guests;
    }

    /**********************************************************************************************/
}
