package com.nomaddeveloper.example.securai.network.model;

public class PostRequest {
    private String title;
    private String body;
    private int userId;

    public PostRequest(final String title, final String body, final int userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }
}
