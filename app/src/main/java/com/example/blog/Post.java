package com.example.blog;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    private  String postId;
    private String title;
    private String description;
    private String timeStamp;
    private String category;



    public Post(){

    }

    public Post(String postId, String title, String description, String timeStamp, String category){
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.category = category;
    }

    public String getPostId() {
        return postId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory(){
        return category;
    }
}
