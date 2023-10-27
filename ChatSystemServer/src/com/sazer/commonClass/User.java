package com.sazer.commonClass;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId; // 用户登录ID
    private String userpwd; // 用户登录密码
    private String userNickname; // 用户昵称

    public User() {
    }

    public User(String userId, String userpwd) {
        this.userId = userId;
        this.userpwd = userpwd;
    }

    public User(String userId, String userpwd, String userNickname) {
        this.userId = userId;
        this.userpwd = userpwd;
        this.userNickname = userNickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserpwd() {
        return userpwd;
    }

    public void setUserpwd(String userpwd) {
        this.userpwd = userpwd;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
