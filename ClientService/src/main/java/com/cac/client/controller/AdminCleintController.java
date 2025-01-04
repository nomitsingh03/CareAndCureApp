package com.cac.client.controller;

import java.util.Map;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminCleintController {

	@Autowired
	public RestTemplate restTemplate;

	/**
	 * Handles requests to the admin registration page.
	 * 
	 * @param model the model to which attributes are added for the view
	 * @return the name of the admin registration page view, which is
	 *         "adminRegistration"
	 */
	@GetMapping("/adminRegistration")
	public String adminRegistrationPage(Model model) {
		model.addAttribute("admin", new AdminDto());
		return "adminRegistration";
	}

	@GetMapping("/adminLoginForm")
	public String adminLoginForm() {
		return "adminLoginForm";
	}

	@GetMapping("/adminHomePage")
	public String adminHomePage(HttpSession session, Model model) {
		session.setAttribute("userRole", "patient");
		String errorMessage = (String) session.getAttribute("errorMessage");
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute(errorMessage);
		}
		String message = (String) session.getAttribute("message");
		if (message != null) {
			model.addAttribute("message", message);
			session.removeAttribute(message);
		}
		return "adminHomePage";
	}
	

	/**
	 * Handles requests to the admin dashboard page.
	 * 
	 * @return the name of the admin page view, which is "adminPage"
	 */
	@GetMapping("/adminPage")
	public String adminPage(HttpSession session, Model model) {
		String errorMessage = (String) session.getAttribute("errorMessage");
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute(errorMessage);
		}
		String message = (String) session.getAttribute("message");
		if (message != null) {
			model.addAttribute("message", message);
			session.removeAttribute(message);
		}
		return "adminPage";
	}

	

	/**
	 * Adds common model attributes for all requests handled by this controller.
	 * 
	 * @param session the HTTP session to retrieve user information
	 * @param model   the model to which attributes are added for the view
	 */
	@ModelAttribute
	public void addModelAttribute(HttpSession session, Model model) {
		String userRole = (String) session.getAttribute("userRole");
		if (userRole != null) {
			model.addAttribute("userRole", userRole);
		}
		String username = (String) session.getAttribute("username");
		if (username != null) {
			model.addAttribute("username", username);
		}
		
	}

	/**
	 * Handles the submission of the admin registration form.
	 * 
	 * @param admin the AdminDto object populated from the registration form
	 * @param model the model to add attributes to for the view
	 * @return the name of the status page view, which indicates the result of the
	 *         registration
	 * @throws JsonMappingException    if there is an error mapping JSON to the
	 *                                 AdminDto object
	 * @throws JsonProcessingException if there is an error processing JSON
	 */
	@PostMapping("/registerAdmin")
	public String submitAdminRegistration(@ModelAttribute("admin") AdminDto admin, Model model)
			throws JsonMappingException, JsonProcessingException {
		AdminDto adminObj = null;
		String requestUrl = "http://localhost:8084/userRegister"; // URL for the admin registration service
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json"); // Set the content type to JSON

		// Create an HTTP entity with the admin data and headers
		admin.setRole("admin");
		HttpEntity<AdminDto> requestEntity = new HttpEntity<>(admin, headers);

		try {
			// Send a POST request to register the admin and capture the response
			ResponseEntity<AdminDto> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
					AdminDto.class);
			adminObj = response.getBody(); // Get the registered admin object from the response
		} catch (HttpClientErrorException e) {
			// Handle client errors by extracting the error message from the response
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
			String errorMessage = rootNode.path("message").asText();
			model.addAttribute("errorMessage", errorMessage); // Add the error message to the model
			return "statusPage"; // Return the status page with the error message
		}
		// Check if the admin object is null, indicating a server issue
		if (adminObj == null) {
			model.addAttribute("errorMessage", "Server Issue. Try Again!!!");
		}
		// Add admin details to the model for display on the status page
		model.addAttribute("username", admin.getUsername());
		model.addAttribute("password", admin.getPassword());
		model.addAttribute("admin", true);
		return "statuspage";

	}

	/**
	 * Handles admin login requests.
	 * 
	 * @param username           the username of the admin
	 * @param password           the password of the admin
	 * @param model              the model to add attributes to for the view
	 * @param session            the HTTP session to store user information
	 * @return a redirect to the admin page if login is successful, or back to the
	 *         login page with an error message
	 */
	@PostMapping("/adminLogin")
	public String adminLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {
		String requestUrl = "http://localhost:8084/login";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json"); // Set the content type to JSON

		LoginDetails details = new LoginDetails(username, password, "admin");
		HttpEntity<LoginDetails> requestEntity = new HttpEntity<>(details, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, String.class);
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
		return "redirect:/adminHomePage";  // Redirect back to the login page in case of failure
	}

	/**
	 * Handles requests to view the admin profile.
	 * 
	 * @param username the username of the admin whose profile is to be viewed
	 * @param model    the model to add attributes to for the view
	 * @return the name of the admin profile view page, which is
	 *         "viewAdminProfilePage"
	 */
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

	@GetMapping("/updatePatientByAdmin")
	public String updatePatient(@RequestParam("id") int patientId, Model model) {
		Patient patient = null;
		String url = "http://localhost:8084/viewPatient/" + patientId;
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.GET, null, Patient.class);
			patient = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patientList";
		}
		if (patient != null) {
			model.addAttribute("patient", patient);
			return "updatePatientByAdminPage";
		} else {
			model.addAttribute("errorMessage", "No Patient found with the given patientId : " + patientId);
			return "patientList";
		}
	}
}
