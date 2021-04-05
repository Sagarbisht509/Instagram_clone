package com.example.instagramclone.Models;

public class Notification {
    private String userId;
    private String notificationText;
    private String postId;
    private Boolean fromPost;

    public Notification() {

    }

    public Notification(String userId, String notificationText, String postId, Boolean fromPost) {
        this.userId = userId;
        this.notificationText = notificationText;
        this.postId = postId;
        this.fromPost = fromPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Boolean getFromPost() {
        return fromPost;
    }

    public void setFromPost(Boolean fromPost) {
        this.fromPost = fromPost;
    }
}
