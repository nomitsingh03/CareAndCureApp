package com.cac.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.LoginDetails;
import com.cac.model.UserInfo;
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
        if(userInfo.getRole().equals("ADMIN")) return createAdmin(userInfo);
        return userRepository.save(userInfo);
    }

    public UserInfo createAdmin(UserInfo userInfo) {
        // userInfo.setRole(userInfo.getRole().toUpperCase());

        UserInfo savedAdmin = userRepository.save(userInfo);

        String subject = "Welcome to Care & Cure – Your Admin Account Details";
        String message = String.format(    "<html>" +
                        "<body style='font-family: Arial, sans-serif; color: #333;'>" +

                        "<p>Dear Admin</b>,</p>" +
                        "<p>We are delighted to welcome you to the <strong>Care & Cure</strong> family. Your admin account has been successfully created, granting you access to our administrative tools and resources.</p>" +
                        "<h3>Your Account Details</h3>" +
                        "<p><strong>Username:</strong> %s</p>" +
                        "<p><strong>Password:</strong> %s</p>" +

                        "<h3>About Care & Cure</h3>" +
                        "<p>Care & Cure is committed to advancing healthcare through innovation, patient-first service, and a focus on compassion and excellence.</p>" +
                        "<h3>Our Mission</h3>" +
                        "<p>To deliver accessible, high-quality medical care while continuously improving patient experiences with advanced technologies and human-centered solutions.</p>" +
                        "<h3>Our Vision</h3>" +
                        "<p>To be a global leader in healthcare, recognized for trust, quality, and pioneering medical research.</p>" +
                        "<h3>Admin Facilities</h3>" +
                        "<ul>" +
                        "  <li>Comprehensive Patient and Staff Management Systems</li>" +
                        "  <li>Advanced Data Analytics and Real-time Reporting</li>" +
                        "  <li>Secure and Scalable Data Storage</li>" +
                        "  <li>Seamless Communication and Collaboration Features</li>" +
                        "  <li>Resource Planning and Scheduling Automation</li>" +
                        "</ul>" +
                        "<p>If you have any questions or need support, please contact us at <a href='mailto:adminsupport@careandcure.com'>adminsupport@careandcure.com</a>. Our team is here to assist you.</p>" +
                        "<p style='font-size: 0.9em; color: #666;'>This is an automated message. Please do not reply to this email. For any inquiries, use the contact information provided above.</p>" +
                        "<p>We look forward to your valuable contributions and are excited about working together to shape the future of healthcare.</p>" +
                        "<p>Warm regards,<br><strong>The Care & Cure Team</strong></p>" +
                        "</body>" +
                        "</html>",
                savedAdmin.getUsername(), savedAdmin.getPassword());

        try{
           emailService.sendEmail(savedAdmin.getEmailId(), subject, message);
        } catch(Exception e){
            userRepository.delete(savedAdmin);
            throw new MailSendException("Failed to send Email");

        }

        return savedAdmin;
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
            System.out.println("doctor");
            return "Welcome Back, " + user.getName()+ "!";
        }
        if ("patient".equalsIgnoreCase(loginDetails.getLoginType()) && user.getRole().equals("PATIENT")) {
            return "Welcome Back, " + user.getPassword() + "!";
        } else {
            throw new UserNotFoundException("Invalid details. Try again!");
        }
    }

}
