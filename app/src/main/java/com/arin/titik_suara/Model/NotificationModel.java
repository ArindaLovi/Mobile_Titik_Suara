package com.arin.titik_suara.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationModel {
    private int id_notifikasi;
    private String category;
    private String message;
    private boolean isRead;
    private long timestamp;

    // Constructor
    public NotificationModel(int id_notifikasi, String category, String message, long timestamp) {
        this.id_notifikasi = id_notifikasi;
        this.category = category;
        this.message = message;
        this.isRead = false;
        this.timestamp = timestamp;
    }

    // Default Constructor
    public NotificationModel(String category, String message, long l) {
        this.isRead = false; // Default to unread
    }

    // Getters and setters
    public int getId_notifikasi() {
        return id_notifikasi;
    }

    public void setId_notifikasi(int id_notifikasi) {
        this.id_notifikasi = id_notifikasi;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Method to format timestamp into a readable date
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "id_notifikasi=" + id_notifikasi +
                ", category='" + category + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", timestamp=" + timestamp +
                ", formattedTimestamp='" + getFormattedTimestamp() + '\'' +
                '}';
    }
}