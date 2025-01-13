package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
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

    private void sendWelcomeEmail(AdminInfo admin) {
        String subject = "Welcome to Care & Cure – Your Admin Account Details";
        String message = String.format(        "<html>" +
                        "<body>" +

                        "<p>Dear Admin</b>,</p>" +
                        "<p>We are delighted to welcome you to the <strong>Care & Cure</strong> family. Your admin account has been successfully created, granting you access to our administrative tools and resources.</p>" +
                        "<h3 color: #FFFFFF>Your Account Details</h3>" +
                        "<p><strong>Username:</strong> %s</p>" +
                        "<p><strong>Password:</strong> %s</p>" +

                        "<h3 color: #FFFFFF>About Care & Cure</h3>" +
                        "<p>Care & Cure is committed to advancing healthcare through innovation, patient-first service, and a focus on compassion and excellence.</p>" +
                        "<h3 color: #FFFFFF>Our Mission</h3>" +
                        "<p>To deliver accessible, high-quality medical care while continuously improving patient experiences with advanced technologies and human-centered solutions.</p>" +
                        "<h3 color: #FFFFFF>Our Vision</h3>" +
                        "<p>To be a global leader in healthcare, recognized for trust, quality, and pioneering medical research.</p>" +
                        "<h3 color: #FFFFFF>Admin Facilities</h3>" +
                        "<ul>" +
                        "  <li>Comprehensive Patient and Staff Management Systems</li>" +
                        "  <li>Advanced Data Analytics and Real-time Reporting</li>" +
                        "  <li>Secure and Scalable Data Storage</li>" +
                        "  <li>Seamless Communication and Collaboration Features</li>" +
                        "  <li>Resource Planning and Scheduling Automation</li>" +
                        "</ul>" +
                        "<p>If you have any questions or need support, please contact us at <a href='mailto:adminsupport@careandcure.com'>adminsupport@careandcure.com</a>. Our team is here to assist you.</p>" +
                        "<p style='font-size: 0.9em; color: #666;'><i>This is an automated message. Please do not reply to this email. For any inquiries, use the contact information provided above.</i></p>" +
                        "<p>We look forward to your valuable contributions and are excited about working together to shape the future of healthcare.</p>" +
                        "<p color: #FFFFFF>Warm regards,<br><strong>The Care & Cure Team</strong></p>" +
                        "<footer style='margin-top: 20px; text-align: center;'>" +
                        "  <img src='https://www.shutterstock.com/image-vector/world-heart-day-concept-happy-family-2143476779' alt='Care & Cure Logo' style='width: 150px; height: auto;'>" +
                        "  <p style='font-size: 0.8em; color: #666;'>© 2024 Care & Cure. All rights reserved.</p>" +
                        "</footer>" +
                        "</body>" +
                        "</html>",
                admin.getUsername(),
                admin.getPassword());

        try{
        emailService.sendEmail(admin.getEmail(), subject, message);
        }
        catch(MessagingException | MailSendException e){
			throw new MailSendException("Failed to send Message");
		}
    }

    // @Transactional
    public AdminInfo createAdmin(AdminInfo adminInfo) throws UserNotFoundException {
        
        AdminInfo savedAdmin = adminRepository.save(adminInfo);
        try {
            // Create user information for admin login
            UserInfo userInfo = new UserInfo(savedAdmin.getUsername(), adminInfo.getPassword(), savedAdmin.getName(), "ADMIN");
            userInfo = userService.createUser(userInfo);

            // Only send email if save was successful
            try{
            sendWelcomeEmail(savedAdmin);
            } catch(Exception e){
                adminRepository.delete(savedAdmin);
                userService.deleteUser(userInfo);
                throw new Exception("Email Notification not working. Try agin after some time");
            }
            
            return savedAdmin;
        } catch (Exception e) {
            System.err.println("Error saving admin: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String loginAdmin(String username, String password) throws UserNotFoundException {
        // AdminInfo adminInfo = adminRepository.findByUsername(username).orElse(()->UserNotFoundException("Admin "));
        return userService.verifyLoginDetails(new LoginDetails(username, password, "admin"));
        
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

    public AdminInfo getAdminInfo(String username) throws UserNotFoundException{
        return adminRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("Admin Not Foud with username: "+ username));
    }

    public AdminInfo getUserByUsername(String username) throws UserNotFoundException {
        AdminInfo userData = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username:" + username));
        return userData;
    }
}
