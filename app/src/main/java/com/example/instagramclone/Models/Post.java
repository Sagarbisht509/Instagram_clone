package com.example.instagramclone.Models;

public class Post {

    private String caption;
    private String imageUrl;
    private String postId;
    private String publisher;

    public Post() {
    }

    public Post(String caption, String imageUrl, String postId, String publisher) {
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.publisher = publisher;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
