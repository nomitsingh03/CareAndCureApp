package com.cac.client.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cac.client.model.LoginDetails;
import com.cac.client.model.Patient;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
@Controller
public class DoctorClientController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${base.url}")
	private String baseUrl;

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

	@GetMapping("/doctorLoginForm")
	public String doctorLoginForm(HttpSession session, Model model) {
		cleanUpSessionAttributes(session, model);
		String userRole = (String) session.getAttribute("userRole");
		if (userRole != null) {
			model.addAttribute("userRole", userRole);
		}
		return "doctorLoginForm";
	}

	@GetMapping("/doctorHomePage")
	public String doctorHomePage(HttpSession session, Model model) {
		session.setAttribute("userRole", "doctor");
		cleanUpSessionAttributes(session, model);
		return "doctorHomePage";
	}

	@PostMapping("/doctorLogin")
	public String doctorLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {

		if (username.isEmpty()) {
			session.setAttribute("errorMessage", "Please enter your username.");
			return "redirect:/doctorLoginForm";
		}
		if (password.isEmpty()) {
			session.setAttribute("errorMessage", "Please enter your password.");
			return "redirect:/doctorLoginForm";
		}
		String requestUrl = baseUrl + "/login";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json"); // Set the content type to JSON

		LoginDetails details = new LoginDetails(username, password, "doctor");
		HttpEntity<LoginDetails> requestEntity = new HttpEntity<>(details, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
					String.class);
			String message = response.getBody();
			session.setAttribute("message", message);
			model.addAttribute("username", username);
			session.setAttribute("userRole", "doctor");
			return "redirect:/doctorHomePage"; // Admin-specific page

		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				session.setAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {

				session.setAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		}
		return "redirect:/doctorLoginForm"; // Redirect back to the login page in case of failure
	}

	// @GetMapping("/updatePatientByDoctor")
	// public String updatePatient(@RequestParam("id") int patientId, Model model) {
	// Patient patient = null;
	// String url = baseUrl+"/viewPatient/" + patientId;
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
	// return "updatePatientByDoctorPage";
	// } else {
	// model.addAttribute("errorMessage", "No Patient found with the given patientId
	// : " + patientId);
	// return "patientList";
	// }
	// }

}
