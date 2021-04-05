package com.example.instagramclone.Models;

public class User {
    private String Name;
    private String Username;
    private String Email;
    private String Image;
    private String Bio;
    private String Id;

    public User() {

    }

    public User(String name, String username, String email, String image, String bio, String id) {
        Name = name;
        Username = username;
        Email = email;
        Image = image;
        Bio = bio;
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
