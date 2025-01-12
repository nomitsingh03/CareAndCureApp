package com.cac.client.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cac.client.model.AdminDto;
import com.cac.client.model.LoginDetails;
import com.cac.client.model.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
public class AdminCleintController {

	@Autowired
	public RestTemplate restTemplate;

	@GetMapping("/adminRegistration")
	public String adminRegistrationPage(Model model) {
		model.addAttribute("admin", new AdminDto());
		return "adminRegistration";
	}

	private void cleanUpSessionAttributes(HttpSession session, Model model) {
		String errorMessage = (String) session.getAttribute("errorMessage");
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute("errorMessage");
		}
		String message = (String) session.getAttribute("message");
		if (message != null) {
			model.addAttribute("message", message);
			session.removeAttribute("message");
		}
	}

	@GetMapping("/adminLoginForm")
	public String adminLoginForm(HttpSession session, Model model) {
		cleanUpSessionAttributes(session, model);
		return "adminLoginForm";
	}

	@GetMapping("/adminHomePage")
	public String adminHomePage(HttpSession session, Model model) {

		cleanUpSessionAttributes(session, model);
		session.setAttribute("userRole", "admin");
		return "adminHomePage";
	}

	@GetMapping("/adminPage")
	public String adminPage(HttpSession session, Model model) {
		cleanUpSessionAttributes(session, model);
		return "adminPage";
	}

	@ModelAttribute
	public void addModelAttribute(HttpSession session, Model model) {
		String username = (String) session.getAttribute("username");
		if (username != null) {
			model.addAttribute("username", username);
		}
	}

	// @PostMapping("/registerAdmin")
	// public String submitAdminRegistration(@ModelAttribute("admin") AdminDto admin, Model model)
	// 		throws JsonMappingException, JsonProcessingException {
	// 	AdminDto adminObj = null;
	// 	String requestUrl = "http://localhost:8084/userRegister"; // URL for the admin registration service
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.set("Content-Type", "application/json"); // Set the content type to JSON

	// 	// Create an HTTP entity with the admin data and headers
	// 	admin.setRole("admin");
	// 	HttpEntity<AdminDto> requestEntity = new HttpEntity<>(admin, headers);

	// 	try {
	// 		// Send a POST request to register the admin and capture the response
	// 		ResponseEntity<AdminDto> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
	// 				AdminDto.class);
	// 		adminObj = response.getBody(); // Get the registered admin object from the response

	// 		// Add admin details to the model for display on the status page
	// 	model.addAttribute("username", admin.getUsername());
	// 	model.addAttribute("email", admin.getEmailId());
	// 	model.addAttribute("admin", true);
	// 	return "statuspage";
	// 	} catch (HttpStatusCodeException e) {
	// 		if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT) {
	// 			// Parse validation errors from the response body
	// 			ObjectMapper objectMapper = new ObjectMapper();
	// 			try {
	// 				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
	// 				model.addAttribute("validationErrors", errors);

	// 			} catch (Exception parseException) {
	// 				model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
	// 			}
	// 			return "registration";
	// 		} else {
	// 			model.addAttribute("errorMessage", "Unexpected error: " + e.getMessage());
	// 		}
	// 		return "statusPage";
	// 	} catch (Exception e) {
	// 		model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
	// 		return "statusPage";
	// 	}		

	// }

	@PostMapping("/registerAdmin")
public String submitAdminRegistration(@ModelAttribute("admin") AdminDto admin, Model model)
        throws JsonMappingException, JsonProcessingException {
    AdminDto adminObj = null;
    String requestUrl = "http://localhost:8084/userRegister"; // URL for the admin registration service
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json"); // Set the content type to JSON

    // Create an HTTP entity with the admin data and headers
    admin.setRole("admin");
	admin.setPassword(generatePassword());
    HttpEntity<AdminDto> requestEntity = new HttpEntity<>(admin, headers);

    try {
        // Send a POST request to register the admin and capture the response
        ResponseEntity<AdminDto> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, AdminDto.class);
        adminObj = response.getBody(); // Get the registered admin object from the response

        // Add admin details to the model for display on the status page
        model.addAttribute("username", admin.getUsername());
        model.addAttribute("email", admin.getEmailId());
        model.addAttribute("admin", true);
        return "statuspage";

    } catch (HttpStatusCodeException e) {
        // Handle specific HTTP status codes
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT) {
            // Parse validation errors from the response body
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
                model.addAttribute("validationErrors", errors);

            } catch (Exception parseException) {
                model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
            }
            return "adminRegistration";
        } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                model.addAttribute("errorMessage", e.getResponseBodyAsString());
            return "statusPage";
        } else {
            model.addAttribute("errorMessage", "Unexpected error: " + e.getMessage());
            return "statusPage";
        }
    } catch (Exception e) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        return "statusPage";
    }
}

	private String generatePassword(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

	@PostMapping("/adminLogin")
	public String adminLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {

		if (username.isEmpty()) {
			session.setAttribute("errorMessage", "Please enter username");
			return "redirect:/adminLoginForm";
		}
		if (password.isEmpty()) {
			session.setAttribute("errorMessage", "Please enter password");
			return "redirect:/adminLoginForm";
		}

		String requestUrl = "http://localhost:8084/login";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json"); // Set the content type to JSON

		LoginDetails details = new LoginDetails(username, password, "admin");
		HttpEntity<LoginDetails> requestEntity = new HttpEntity<>(details, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
					String.class);
			String message = response.getBody();
			session.setAttribute("message", message);
			session.setAttribute("username", username);
			session.setAttribute("userRole", "admin");
			return "redirect:/adminPage"; // Admin-specific page

		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				System.out.println(errorMessage.get("error"));
				session.setAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				System.out.println(parseException.getMessage());
				session.setAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		}
		return "redirect:/adminLoginForm"; // Redirect back to the login page in case of failure
	}

	@GetMapping("/viewAdminProfile")
	public String viewProfile(@RequestParam String username, Model model) {

		AdminDto dto = null;
		String url = "http://localhost:8084/viewUserInfo/" + username;
		try {
			ResponseEntity<AdminDto> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					AdminDto.class);
			dto = response.getBody();
			model.addAttribute("admin", dto);

		} catch (HttpStatusCodeException e) {
			model.addAttribute("errorMessage", "Unable to fetch Admin details. Please try again later.");
		}
		return "viewAdminProfilePage";
	}

	// @GetMapping("/updatePatientByAdmin")
	// public String updatePatient(@RequestParam("id") int patientId, Model model) {
	// Patient patient = null;
	// String url = "http://localhost:8084/viewPatient/" + patientId;
	// try {
	// ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.GET,
	// null, Patient.class);
	// patient = response.getBody();
	// } catch (HttpClientErrorException | HttpServerErrorException e) {
	// model.addAttribute("errorMessage",
	// "Unable to fetch Patient with Id (" + patientId + "). Please try again
	// later.");
	// return "patientList";
	// }
	// if (patient != null) {
	// model.addAttribute("patient", patient);
	// return "updatePatientByAdminPage";
	// } else {
	// model.addAttribute("errorMessage", "No Patient found with the given patientId
	// : " + patientId);
	// return "patientList";
	// }
	// }
}
