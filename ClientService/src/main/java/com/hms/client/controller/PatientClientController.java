package com.hms.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.client.model.Patient;

import jakarta.servlet.http.HttpSession;

@Controller
public class PatientClientController {
	
	@Autowired
	private RestTemplate restTemplate;

@ModelAttribute
public void addUserRoleToModel(HttpSession session, Model model) {
    String userRole = (String) session.getAttribute("userRole");
    if (userRole != null) {
        model.addAttribute("userRole", userRole);
    }
	Integer patientId = (Integer)session.getAttribute("patientId");
	if(patientId!=null) model.addAttribute("patientId", patientId);
}	
	
	@GetMapping("/")
	public String homePage() {
		return "homePage";
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

	@GetMapping("/adminPage")
	public String adminPage(){
		return "adminPage";
	}

	@GetMapping("/patientPage")
	public String patientPage(Model model) {
		return "patientPage";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	
	@PostMapping("/registerPatient")
	public String submitPatientRegistration(@ModelAttribute("patient") Patient patient, Model model) throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl ="http://localhost:8084/registerPatient";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(reuestUrl, HttpMethod.POST, requestEntity, Patient.class);
			patientObj = response.getBody();
		} catch(HttpClientErrorException e) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
			String errorMessage = rootNode.path("message").asText();
			model.addAttribute("errorMessage", errorMessage);
			return "statusPage";
		}
		if(patientObj==null)
		{
			model.addAttribute("errorMessage", "Server Issue.Try Again!!!");
		}
		model.addAttribute("patientId", patientObj.getPatientId());
		model.addAttribute("patientName", patientObj.getPatientName());
		return "statuspage";
		
	}

	@RequestMapping(value = "/findPatientByName", method = RequestMethod.GET)
	public String findPatientByName(@RequestParam("name") String name, Model model) {
	    List<Patient> patientList = new ArrayList<>();
	    String url = "http://localhost:8084/viewPatientByName/" + name;
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
	    try {
	        ResponseEntity<List<Patient>> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                requestEntity,
	                new ParameterizedTypeReference<List<Patient>>() {}
	        );
	        patientList = response.getBody();
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
	        return "patientSearch";
	    }

	    if (patientList!=null && patientList.size()!=0) {
	        model.addAttribute("patientList", patientList);
	        return "patientList";
	    } else {
	        model.addAttribute("errorMessage", "No Patient found with the given name." + name);
	        return "patientSearch";
	    }
	}

	@RequestMapping(value = "/findPatientById", method = RequestMethod.GET)
	public String findPatientById(@RequestParam("patientId") int patientId, Model model) {
	    Patient patient=null;
	    String url = "http://localhost:8084/viewPatient/" + patientId;

	    try {
	        ResponseEntity<Patient> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                null,
	                Patient.class
	        );
	        patient = response.getBody();
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
	        return "patientSearch";
	    }
	    if (patient!=null) {
			List<Patient> patientList = new ArrayList<>();
			patientList.add(patient);
	        model.addAttribute("patientList", patientList);
	        return "patientList";
	    } else {
	        model.addAttribute("errorMessage", "No Patient found with the given patientId : "+ patientId);
	        return "patientSearch";
	    }
	}

	@GetMapping("/viewPatientProfile")
	public String viewProfile(@RequestParam int patientId, Model model) {
		Patient patient=null;
	    String url = "http://localhost:8084/viewPatient/" + patientId;
	    try {
	        ResponseEntity<Patient> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                null,
	                Patient.class
	        );
	        patient = response.getBody();
			model.addAttribute("patient", patient);
			return "viewProfilePage";
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
	        return "viewProfilePage";
	    }
	}
	

	@GetMapping("/updatePatient")
	public String updatePatient(@RequestParam("id") int patientId, Model model) {
		Patient patient=null;
	    String url = "http://localhost:8084/viewPatient/" + patientId;
	    try {
	        ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.GET, null, Patient.class);
	        patient = response.getBody();
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
	        return "patientList";
	    }
	    if (patient!=null) {
	        model.addAttribute("patient", patient);
	        return "updatePage";
	    } else {
	        model.addAttribute("errorMessage", "No Patient found with the given patientId : "+ patientId);
	        return "patientList";
	    }
	}
	
	@PostMapping("/updatePatient")
	public String submitUpdatePatient(@ModelAttribute("patient") Patient patient, Model model) throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl ="http://localhost:8084/updatePatient/"+patient.getPatientId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		
		try {
			ResponseEntity<Patient> response= restTemplate.exchange(reuestUrl, HttpMethod.PUT, requestEntity, Patient.class);
			patientObj = response.getBody();
		} catch(HttpClientErrorException e) {
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
			String errorMessage = rootNode.path("message").asText();
			model.addAttribute("errorMessage", errorMessage);
			model.addAttribute("patient", patient);
			return "updatePage";
		}
		
		
			model.addAttribute("patient", patientObj);
			model.addAttribute("succMessage", " Patient updated Successfully!");
		return "updatePage";
		
	}

	@GetMapping("/deactivatePatient")
	public String deactivatePAtient(@RequestParam("patientId") int patientId, Model model) {
		Patient patient=null;
	    String url = "http://localhost:8084/deactivatePatient/" + patientId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
	    HttpEntity<Patient> requestEntity = new HttpEntity<>(patient,headers);
	    try {
	        ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Patient.class);
	        patient = response.getBody();
			return getAllPatient(model);
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
	        return "patientList";
		}   
}

    @RequestMapping(value = "/viewAllPatient", method = RequestMethod.GET)
	public String getAllPatient( Model model) {
	    List<Patient> patientList = new ArrayList<>();
	    String url = "http://localhost:8084/viewAllPatient";
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
		HttpEntity<List<Patient>> requestEntity = new HttpEntity<>(patientList, headers);
	    try {
	        ResponseEntity<List<Patient>> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                requestEntity,
	                new ParameterizedTypeReference<List<Patient>>() {}
	        );
	        patientList = response.getBody();
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("errorMessage", "Unable to fetch Patient List. Please try again later.");
	        return "patientList";
	    }

	    if (patientList!=null && patientList.size()!=0) {
	        model.addAttribute("patientList", patientList);
	        return "patientList";
	    } else {
	        model.addAttribute("errorMessage", "No Patient Record Found.");
	        return "patientList";
	    }
	}

	@PostMapping("/patientLogin")
	public String patientLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
		int patientId = 0;
		if(username.isEmpty() || password.isEmpty()) {
			model.addAttribute("errorMessage", "Please enter Patient Id and Password.");
			return "homePage";
		}
		try{
			patientId = Integer.parseInt(username);
		} catch (NumberFormatException e) {
			model.addAttribute("errorMessage", "Invalid patient Id / username. Please enter a valid Patient Id.");
			return "homePage";
		}

		String requestUrl = "http://localhost:8084/patientLogin?username=" + patientId + "&password=" + password;

		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST,null, String.class);
			String message = response.getBody();
			System.out.println(patientId);
			model.addAttribute("message", message);
			session.setAttribute("patientId", patientId);
			session.setAttribute("userRole", "patient");
			return "redirect:/patientPage";        // Admin-specific page
			
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getResponseBodyAsString().isEmpty()) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					model.addAttribute("errorMessage", "Invalid credentials. Please check your username and password.");
				} else {
					model.addAttribute("errorMessage", "An error occurred with the login request. Status: " + e.getStatusCode());
				}
			} else {
				// In case there is a response body
				model.addAttribute("errorMessage", "Error: " + e.getResponseBodyAsString());
			}
		}
		return "homePage"; // Redirect back to the login page in case of failure
	}

	@PostMapping("/adminLogin")
	public String adminLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
		String requestUrl = "http://localhost:8084/adminLogin?username=" + username + "&password=" + password;
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, null, String.class);
			String message = response.getBody();
			session.setAttribute("userRole", "admin");
			model.addAttribute("message", message);
			return "redirect:/adminPage"; // Admin-specific page
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getResponseBodyAsString().isEmpty()) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                model.addAttribute("errorMessage", "Invalid credentials. Please check your username and password.");
            } else {
                model.addAttribute("errorMessage", "An error occurred with the login request. Status: " + e.getStatusCode());
            }
        } else {
            // In case there is a response body
            model.addAttribute("errorMessage", "Error: " + e.getResponseBodyAsString());
        }
		}
	
		return "homePage"; // Redirect back to the login page in case of failure
	}	
	
}
