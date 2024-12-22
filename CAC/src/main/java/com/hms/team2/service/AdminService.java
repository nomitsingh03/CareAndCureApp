package com.hms.team2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.team2.model.Admin;
import com.hms.team2.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    
    public Admin createAdmin(Admin adminObj){
        return adminRepository.save(adminObj);
    }

    public Admin getByUsername(String username){
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        return admin;
    }
}
