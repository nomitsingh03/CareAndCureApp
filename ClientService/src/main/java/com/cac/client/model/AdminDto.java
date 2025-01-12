package com.cac.client.model;

public class AdminDto {
    
    private int id;
    private String username;
    private String emailId;
    private String name;
    private String role;
    private String password;

    public AdminDto(){
        
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String adminName) {
        this.role = adminName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPassword(String password){
        this.password=password;
    }

    public String getPassword(){
        return password;
    }

}
