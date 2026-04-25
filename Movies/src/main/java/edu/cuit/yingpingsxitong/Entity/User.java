package edu.cuit.yingpingsxitong.Entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer userId;
    private String username;
    private String nickname;
    private String avatar;
    private String password;
    private String email;
    private Boolean manager;
    private Boolean permission;

    // Constructors
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getManager() {
        return manager;
    }

    public void setManager(Boolean manager) {
        this.manager = manager;
    }

    public Boolean getPermission() {
        return permission;
    }

    public void setPermission(Boolean permission) {
        this.permission = permission;
    }
}