package com.cac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cac.exception.UserNotFoundException;
import com.cac.model.AdminInfo;
import com.cac.service.AdminService;

import jakarta.validation.Valid;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Register Admin with manual saving for testing and service call
    @PostMapping("/registerAdmin")
    public ResponseEntity<AdminInfo> registerAdmin(@Valid @RequestBody AdminInfo adminInfo) throws UserNotFoundException {
        AdminInfo finalAdmin = adminService.createAdmin(adminInfo);
        return new ResponseEntity<>(finalAdmin, HttpStatus.CREATED);
    }

    // Admin Login
    @PostMapping("/loginAdmin")
    public ResponseEntity<String> loginAdmin(@RequestParam String username, @RequestParam String password) throws UserNotFoundException {
        String message = adminService.loginAdmin(username, password);
        return ResponseEntity.ok(message);
    }

    // Update Admin details
    @PutMapping("/updateAdmin/{id}")
    public ResponseEntity<AdminInfo> updateAdmin(@PathVariable int id, @Valid @RequestBody AdminInfo adminInfo)
            throws Exception {
        return new ResponseEntity<AdminInfo>(adminService.updateAdmin(id, adminInfo), HttpStatus.ACCEPTED);
    }
}
