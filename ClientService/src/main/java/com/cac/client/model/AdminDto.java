package com.cac.client.model;

public class AdminDto {
    
    private int id;
    private String username;
    private String email;
    private String name;
    private String password;

    public AdminDto(){
        
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String emailId) {
        this.email = emailId;
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
