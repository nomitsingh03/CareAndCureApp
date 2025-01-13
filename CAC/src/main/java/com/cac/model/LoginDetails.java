package com.cac.model;

public class LoginDetails {
    
    private String username;
    private String password;
    private String loginType;

    

    public LoginDetails(String username, String password, String loginType) {
        this.username = username;
        this.password = password;
        this.loginType = loginType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLoginType() {
        return loginType;
    }

}
