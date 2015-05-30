package by.bsu.fpmi.chat;

import java.lang.Override;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String messageId;
    private String username;
    private String user;
    private String messageText;
    private String date;
    private String status;

    public Message(String messageId, String username, String user, String messageText, String status) {
        this.messageId = messageId;
        this.username = username;
        this.user = user;
        this.messageText = messageText;
        Date nowDate = new Date();
        SimpleDateFormat ftDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.date = ftDate.format(nowDate);
        this.status = status;
    }

    public Message(String messageId, String username, String user, String messageText, String date, String status) {
        this.messageId = messageId;
        this.username = username;
        this.user = user;
        this.messageText = messageText;
        this.date = date;
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ("{\"date\":\""+this.date+"\",\"username\":\""+this.username+"\",\"user\":\""+this.user+"\",\"messageId\":\""+this.messageId+
                "\",\"messageText\":\""+this.messageText+"\",\"status\":\""+this.status+"\"}");
    }
}