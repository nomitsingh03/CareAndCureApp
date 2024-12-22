package com.hms.team2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.team2.model.Admin;
import com.hms.team2.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/adminRegister")
	public ResponseEntity<Admin> adminLogin(@RequestBody Admin admin) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(admin));
	}
    

    @PostMapping("/adminLogin")
	public ResponseEntity<Admin> adminLogin(@RequestParam String username, @RequestParam String password) {
        Admin admin = adminService.getByUsername(username);
		System.out.println(admin.toString());
		System.out.println(username+"  "+password);
		if(admin.getUsername().equals(username) && admin.getPassword().equals(password)) return ResponseEntity.ok(admin);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}

	@GetMapping("/viewAdmin/{username}")
	public ResponseEntity<Admin> getMethodName(@PathVariable String username) {
		Admin admin = adminService.getByUsername(username);
		if(admin==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		return ResponseEntity.ok(admin);
	}
	
}
