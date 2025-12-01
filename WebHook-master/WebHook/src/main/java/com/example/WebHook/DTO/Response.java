package com.example.WebHook.DTO;

public class Response {

    // Field names should match JSON keys from generateWebhook API
    // You can adjust names later if needed.
    private String webhook;
    private String accessToken;
    private Integer question;

    public Response() {
        // no-args constructor
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getQuestion() {
        return question;
    }

    public void setQuestion(Integer question) {
        this.question = question;
    }
}
