package com.cac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cac.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>{

    Admin save(Admin admin);

    Optional<Admin> findByUsername(String username);
    
} 
