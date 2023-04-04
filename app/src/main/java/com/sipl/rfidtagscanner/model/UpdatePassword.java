package com.sipl.rfidtagscanner.model;

public class UpdatePassword {
    String previousPassword;
    String newPassword;
    String Username;
    String message;

    public UpdatePassword(String previousPassword, String newPassword, String username, String message) {
        this.previousPassword = previousPassword;
        this.newPassword = newPassword;
        Username = username;
        this.message = message;
    }

    public String getPreviousPassword() {
        return previousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
