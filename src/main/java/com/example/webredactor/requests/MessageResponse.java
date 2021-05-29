package com.example.webredactor.requests;

public class MessageResponse {

    private Long fileId;
    private String message;

    public MessageResponse(String message, long id) {
        this.message = message;
        this.fileId = id;
    }

    public String getMessage() {
        return message;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}