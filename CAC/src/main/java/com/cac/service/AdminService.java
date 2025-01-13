package com.cac.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.AdminInfo;
import com.cac.model.LoginDetails;
import com.cac.model.UserInfo;
import com.cac.repository.AdminRepository;

import jakarta.mail.MessagingException;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private void sendWelcomeEmail(AdminInfo admin, String password) throws MessagingException {
        String subject = "Your Admin Account has been Created";
        String text = String.format(
                "Dear %s,\n\n" +
                        "Your admin account has been successfully created.\n\n" +
                        "Username: %s\n" +
                        "Temporary Password: %s\n\n" +
                        "Please login and change your password as soon as possible.\n\n" +
                        "Best regards,\nSystem Admin",
                admin.getName(),
                admin.getUsername(),
                password);

        emailService.sendEmail(admin.getEmail(), subject, text);
    }

    // @Transactional
    public AdminInfo createAdmin(AdminInfo adminInfo) throws UserNotFoundException {
        System.out.println(adminInfo.getUsername());
        // Check if username already exists
        // if(adminRepository.findByUsername(adminInfo.getUsername())!=null) throw new
        // UserNotFoundException("Username already Exist.");
        // Save admin details to the database
        AdminInfo savedAdmin = adminRepository.save(adminInfo);
        try {

            // Create user information for admin login
            UserInfo userInfo = new UserInfo(savedAdmin.getUsername(), adminInfo.getPassword(), savedAdmin.getName(),
                    "admin");
            userService.createUser(userInfo);

            // Only send email if save was successful
            sendWelcomeEmail(savedAdmin, savedAdmin.getPassword());

            return savedAdmin;
        } catch (Exception e) {
            System.err.println("Error saving admin: " + e.getMessage());
            throw new RuntimeException("Failed to save admin: " + e.getMessage());
        }
    }

    // Method for admin login (checks password)
    public AdminInfo loginAdmin(String username, String password) throws UserNotFoundException {
        // AdminInfo adminInfo =
        // adminRepository.findByUsername(username).orElse(()->UserNotFoundException("Admin
        // "));
        try {
            userService.verifyLoginDetails(new LoginDetails(username, password, "admin"));
        } catch (UserNotFoundException e) {
            throw e;
        }
        return getAdminInfo(username);

    }

    // Method to update admin details
    public AdminInfo updateAdmin(int id, AdminInfo updatedAdminInfo) throws Exception {
        AdminInfo existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new Exception("Admin not found"));

        existingAdmin.setName(updatedAdminInfo.getName());
        existingAdmin.setUsername(updatedAdminInfo.getUsername());
        existingAdmin.setEmail(updatedAdminInfo.getEmail());

        return adminRepository.save(existingAdmin);
    }

    public AdminInfo getAdminInfo(String username) throws UserNotFoundException {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Admin Not Found with username: " + username));
    }

}
