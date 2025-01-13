package com.cac.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.LoginDetails;
import com.cac.model.Patient;
import com.cac.model.UserInfo;
import com.cac.repository.PatientRepository;
import com.cac.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public UserInfo createUser(UserInfo userInfo) {
        userInfo.setRole(userInfo.getRole().toUpperCase());
        return userRepository.save(userInfo);
    }


    public UserInfo getUserByUsername(String username) throws UserNotFoundException {
        UserInfo userData = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username:" + username));
        return userData;
    }

    public String verifyLoginDetails(LoginDetails loginDetails) throws UserNotFoundException {
        
        UserInfo user = userRepository.findByUsername(loginDetails.getUsername()).orElseThrow(
                () -> new UserNotFoundException("User not found with username:" + loginDetails.getUsername()));

        if (!user.getPassword().equals(loginDetails.getPassword())) {
            throw new UserNotFoundException("Invalid username or password. Try again!");
        }

        // Check if the role matches the login type
        if (("admin".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("ADMIN"))) {
            return "Welcome Back, " + user.getName()+ "!";
        }
        if (("doctor".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("DOCTOR"))) {
            
            return "Welcome Back, " + user.getName()+ "!";
        }
        if ("patient".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("PATIENT")) {
            return "Welcome Back, " + user.getPassword() + "!";
        } else {
            throw new UserNotFoundException("Invalid details. Try again!");
        }

    }

    public void deleteUser(UserInfo userInfo) {
        userRepository.delete(userInfo);
    }

}
