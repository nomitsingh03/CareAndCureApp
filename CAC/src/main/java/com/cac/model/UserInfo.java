package com.cac.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class UserInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username required")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;

    @Email(message = "Enter valid email", regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$")
	// @Column(unique = true)
	private String emailId;

    @Column(nullable = false)
    private String role;

    public UserInfo(){
        
    }

    public UserInfo(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserInfo(String username, String password, String role, String email){
        this.username = username;
        this.password = password;
        this.role = role;
        this.emailId=email;
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
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getRole(){
        return this.role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailId(String email){
        this.emailId = email;
    }

    public String getEmailId(){
        return this.emailId;
    }
    
}
