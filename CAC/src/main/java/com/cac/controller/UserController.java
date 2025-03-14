package com.cac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cac.exception.UserNotFoundException;
import com.cac.model.LoginDetails;
import com.cac.model.UserInfo;
import com.cac.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    

    @PostMapping("/userRegister")
    public ResponseEntity<UserInfo> userLogin(@Valid @RequestBody UserInfo user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginDetails loginDetails) throws UserNotFoundException {
                String loginMessage = userService.verifyLoginDetails(loginDetails);

        return new ResponseEntity<>(loginMessage, HttpStatus.OK);
    }

    @GetMapping("/viewUserInfo/{username}")
    public ResponseEntity<UserInfo> getMethodName(@PathVariable String username) throws UserNotFoundException {
        UserInfo userInfo = userService.getUserByUsername(username);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

}
