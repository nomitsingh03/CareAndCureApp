package com.cac.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.cac.client.model.LoginDetails;
import com.cac.client.model.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class PatientClientController {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${base.url}")
    private String baseUrl;

	@ModelAttribute
	public void addUserRoleToModel(HttpSession session, Model model) {
		String userRole = (String) session.getAttribute("userRole");
		if (userRole != null) {
			model.addAttribute("userRole", userRole);
		}
		Integer patientId = (Integer) session.getAttribute("patientId");
		if (patientId != null) {
			model.addAttribute("patientId", patientId);
		}
	}

	@GetMapping("/")
	public String homePage(HttpSession session, Model model) {
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
		return "homePageView";
	}

	@GetMapping("/patientRegistration")
	public String registrationPage(Model model) {
		model.addAttribute("patient", new Patient());

		return "registration";
	}

	@GetMapping("/searchPatient")
	public String searchPatient() {
		return "patientSearch";
	}

	@GetMapping("/patientLoginForm")
	public String patientLoginForm() {
		return "patientLoginForm";
	}

	@GetMapping("/patientHomePage")
	public String patientHomePage(HttpSession session, Model model) {
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
		return "patientHomePage";
	}

	@GetMapping("/patientPage")
	public String patientPage(HttpSession session,Model model) {
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
		return "patientPage";
	}
	/*@GetMapping("/adminPage")
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
	} */

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@PostMapping("/registerPatient")
	public String submitPatientRegistration(@ModelAttribute("patient") Patient patient, Model model)
			throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl = baseUrl+"/registerPatient";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

		try {
			ResponseEntity<Patient> response = restTemplate.postForEntity(reuestUrl, requestEntity,
					Patient.class);
			patientObj = response.getBody();
			model.addAttribute("patientId", patientObj.getPatientId());
		model.addAttribute("patientName", patientObj.getPatientName());
		model.addAttribute("message", "Registration Successfully.");
		return "statuspage";
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode()==HttpStatus.CONFLICT) {
				// Parse validation errors from the response body
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
					model.addAttribute("validationErrors", errors);
					
				} catch (Exception parseException) {
					model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
				}
				return "registration";
			} else {
				model.addAttribute("errorMessage", "Unexpected error: " + e.getMessage());
			}
			return "statusPage";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
			return "statusPage";
		}
		

	}

	@RequestMapping(value = "/findPatientByName", method = RequestMethod.GET)
	public String findPatientByName(@RequestParam("name") String name, Model model) {
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl+"/viewPatientByName/" + name;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				model.addAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}

		}
		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			// Add search type and value to the model
            model.addAttribute("searchType", "name");
            model.addAttribute("searchValue", name);
			return "patientList";
		} 
			return "patientSearch";
	}

	@RequestMapping(value = "/findPatientById", method = RequestMethod.GET)
	public String findPatientById(@RequestParam("patientId") int patientId, Model model) {
		Patient patient = null;
		String url = baseUrl+"/viewPatient/" + patientId;

		try {
			ResponseEntity<Patient> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					Patient.class);
			patient = response.getBody();

			if (patient != null) {
				List<Patient> patientList = new ArrayList<>();
				patientList.add(patient);
				model.addAttribute("patientList", patientList);

				// Add search type and value to the model
				model.addAttribute("searchType", "id");
				model.addAttribute("searchValue", patientId);
				return "patientList";
			}

		} catch (HttpStatusCodeException e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				model.addAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				model.addAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		} catch(Exception e){
			model.addAttribute("errorMessage", "Some Internal Error Occur ");
		}
		return "patientSearch";
	}

	@GetMapping("/viewPatientProfile")
	public String viewProfile(@RequestParam int patientId, Model model) {
		Patient patient = null;
		String url = baseUrl+"/viewPatient/" + patientId;
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					Patient.class);
			patient = response.getBody();
			model.addAttribute("patient", patient);
			return "viewProfilePage";
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "viewProfilePage";
		}
	}


	@GetMapping("/updatePatient")
	public String updatePatient(@RequestParam("id") int patientId, Model model) {
		Patient patient = null;
		String url = baseUrl+"/viewPatient/" + patientId;
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
			return "updatePatient";
		} else {
			model.addAttribute("errorMessage", "No Patient found with the given patientId : " + patientId);
			return "patientList";
		}
	}

	@PostMapping("/updatePatient")
	public String submitUpdatePatient(@ModelAttribute("patient") Patient patient, Model model)
			throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl = baseUrl+"/updatePatient/" + patient.getPatientId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
				
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

		try {
			ResponseEntity<Patient> response = restTemplate.exchange(reuestUrl, HttpMethod.PUT, requestEntity,
					Patient.class);
			patientObj = response.getBody();
		} catch (HttpClientErrorException e) {

			ObjectMapper objectMapper = new ObjectMapper();
					try {
						Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
					model.addAttribute("validationErrors", errors);
					} catch (Exception ex) {
						JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
						String errorMessage = rootNode.path("message").asText();
						model.addAttribute("errorMessage", errorMessage);
					}
			
			model.addAttribute("patient", patient);
			return "updatePatient";
		} 

		model.addAttribute("patient", patientObj);
		model.addAttribute("succMessage", " Patient updated Successfully!");
		return "updatePatient";

	}

	@GetMapping("/deactivatePatient")
	public String deactivatePatient(@RequestParam("patientId") int patientId,
	@RequestParam(value = "searchType", required = false) String searchType,
	@RequestParam(value = "searchValue", required = false) String searchValue,
	Model model) {
		Patient patient = null;
		String url = baseUrl+"/deactivatePatient/" + patientId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Patient.class);
			patient = response.getBody();

			// Determine success message based on the patient's status
			if (patient != null && patient.isActive()) {
				model.addAttribute("successMessage", "Patient with ID (" + patientId + ") activated successfully.");
			} else {
				model.addAttribute("errorMessage", "Patient with ID (" + patientId + ") deactivated successfully.");
			}
	
			// Redirect based on the previous search type
			if ("id".equalsIgnoreCase(searchType)) {
				return findPatientById(patientId, model);
			} else if ("name".equalsIgnoreCase(searchType) && searchValue != null) {
				return findPatientByName(searchValue, model);
			} else if("viewProfile".equals(searchType)){
				return viewProfile(patientId, model);
			} else {
				// Default behavior: load all patients
				return getAllPatient(model);
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patientList";
		}
	}

	
	@RequestMapping(value = "/viewAllPatient", method = RequestMethod.GET)
	public String getAllPatient(Model model) {
		List<Patient> patientList = new ArrayList<>();
		String url = baseUrl+"/viewAllPatient";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
		try {
			ResponseEntity<List<Patient>> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<List<Patient>>() {
					});
			patientList = response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
			return "patientList";
		}

		if (patientList != null && patientList.size() != 0) {
			model.addAttribute("patientList", patientList);
			return "patientList";
		} else {
			model.addAttribute("errorMessage", "No Patient Record Found.");
			return "patientList";
		}
	}


	@PostMapping("/patientLogin")
	public String patientLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {
		if (username.isEmpty() || password.isEmpty()) {
			model.addAttribute("errorMessage", "Please enter Patient Id and Password.");
			return "patientHomePage";
		}
		LoginDetails details = new LoginDetails(username, password, "patient");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<LoginDetails> requestEntity = new HttpEntity<>(details, headers);

		String requestUrl = baseUrl+"/login";

		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, String.class);
			String message = response.getBody();
			session.setAttribute("message", message);
			session.setAttribute("patientId", Integer.parseInt(username));
			session.setAttribute("userRole", "patient");
			return "redirect:/patientPage"; // Admin-specific page

		} catch (HttpStatusCodeException e) {
			System.out.println(e.getMessage());
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errorMessage = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
				
				session.setAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				
				session.setAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		}
		return "redirect:/patientHomePage"; // Redirect back to the login page in case of failure
	}

}
