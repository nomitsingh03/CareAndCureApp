package com.hms.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
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
	
	@PostMapping("/registerPatient")
	public String submitPatientRegistration(@ModelAttribute("patient") Patient patient, Model model) throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl ="http://localhost:8084/registerPatient";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		
		try {
			ResponseEntity<Patient> response= restTemplate.exchange(reuestUrl, HttpMethod.POST, requestEntity, Patient.class);
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
	

}
