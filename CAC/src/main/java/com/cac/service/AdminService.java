package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.model.Admin;
import com.cac.repository.AdminRepository;

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
