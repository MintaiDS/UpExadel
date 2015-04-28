package by.bsu.fpmi.chat;

import java.lang.Override;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String id;
    private String author;
    private String text;
    private String date;

    public Message(String id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
        Date nowDate = new Date();
        SimpleDateFormat ftDate = new SimpleDateFormat("dd-mm-yyyy HH:mm");
        this.date = ftDate.format(nowDate);
    }

    public Message(String id, String author, String text, String date) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return this.date+"\t"+this.author+" : "+this.text;
    }
}