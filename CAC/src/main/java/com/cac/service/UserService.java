package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.LoginFailedException;
import com.cac.exception.UserNotFoundException;
import com.cac.model.LoginDetails;
import com.cac.model.UserInfo;
import com.cac.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserInfo createUser(UserInfo userInfo) {
        userInfo.setRole(userInfo.getRole().toUpperCase());
        return userRepository.save(userInfo);
    }

    public UserInfo getUserByUsername(String username) throws UserNotFoundException {
        UserInfo userData = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username:" + username));
        return userData;
    }

    public String verifyLoginDetails(LoginDetails loginDetails) throws LoginFailedException, UserNotFoundException {
        UserInfo user = userRepository.findByUsername(loginDetails.getUsername()).orElseThrow(
                () -> new UserNotFoundException("User not found with username:" + loginDetails.getUsername()));
        if (!user.getPassword().equals(loginDetails.getPassword())) {
            throw new LoginFailedException("Invalid username or password. Try again!");
        }

        // Check if the role matches the login type
        if (("admin".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("ADMIN"))) {
            return "Welcome Back, " + user.getName()+ "!";
        }
        if ("patient".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("PATIENT")) {
            return "Welcome Back, " + user.getPassword() + "!";
        } else {
            throw new LoginFailedException("Invalid details. Try again!");
        }
    }

}
