package com.hms.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

@Controller
public class PatientClientController {
	
	@Autowired
	private RestTemplate restTemplate;
	
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
	    HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

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
System.out.println(patient);
	    if (patient!=null) {
	        model.addAttribute("patient", patient);

	        return "updatePage";
	    } else {
	        model.addAttribute("errorMessage", "No Patient found with the given patientId : "+ patientId);
	        return "patientSearch";
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
	        return "patientList";
		}
	    if (patient!=null) {
	        model.addAttribute("successMessage", "Patient with Id : "+ patientId + " is deactivated.");

	        return "patientList";
	    } else {
	        model.addAttribute("errorMessage", "Some Internal Error occur .Try agin later!");
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
}
