package com.example.myshots.Models;

public
class dataholder {

    String name, note, pimage;
    Long timestamp;

    public
    dataholder(String name, String note, String pimage) {
        this.name = name;
        this.note = note;
        this.pimage = pimage;
    }

    public
    dataholder(String name, String note, String pimage, Long timestamp) {
        this.name = name;
        this.note = note;
        this.pimage = pimage;
        this.timestamp = timestamp;
    }

    public
    Long getTimestamp() {
        return timestamp;
    }


    public
    void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public
    String getName() {
        return name;
    }

    public
    void setName(String name) {
        this.name = name;
    }

    public
    String getNote() {
        return note;
    }

    public
    void setNote(String note) {
        this.note = note;
    }

    public
    String getPimage() {
        return pimage;
    }

    public
    void setPimage(String pimage) {
        this.pimage = pimage;
    }
}
