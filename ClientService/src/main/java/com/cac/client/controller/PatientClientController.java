package com.cac.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PutMapping;
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

	/**
	 * Adds user role and patient ID to the model from the current HTTP session.
	 * 
	 * This method is annotated with @ModelAttribute, which means it will be
	 * invoked before any request handling methods in the controller. It checks
	 * the current HTTP session for the attributes "userRole" and "patientId".
	 * If these attributes are present, they are added to the model for use in
	 * the view. The "patientId" attribute is also removed from the session
	 * after being added to the model to prevent it from being reused in future
	 * requests.
	 * 
	 * @param session the current HTTP session containing user attributes
	 * @param model   the model to which attributes will be added for the view
	 */
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

	/**
	 * Handles requests to the home page.
	 * 
	 * @return the name of the home page view i.e. homePage.html
	 */
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
		return "homePage";
	}

	/**
	 * Handles requests to the patient registration page.
	 * 
	 * @param model the model to which attributes are added for the view
	 * @return the name of the patient registration page view, which is
	 *         "registration"
	 */
	@GetMapping("/patientRegistration")
	public String registrationPage(Model model) {
		model.addAttribute("patient", new Patient());

		return "registration";
	}

	/**
	 * Handles requests to the patient search page.
	 * 
	 * @return the name of the patient search page view, which is "patientSearch"
	 */
	@GetMapping("/searchPatient")
	public String searchPatient() {
		return "patientSearch";
	}

	/**
	 * Handles requests to the patient details page.
	 * 
	 * @param model the model to add attributes to for the view
	 * @return the name of the patient page view, which is "patientPage"
	 */
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

	/**
	 * Handles user logout requests.
	 * 
	 * @param session the HTTP session to be invalidated
	 * @return a redirect to the home page view
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	/**
	 * Handles the submission of the patient registration form.
	 * 
	 * @param patient the patient object populated from the form data
	 * @param model   the model to add attributes to for the view
	 * @return the name of the status page view, which indicates the result of the
	 *         registration
	 * @throws JsonMappingException    if there is an error mapping JSON to the
	 *                                 Patient object
	 * @throws JsonProcessingException if there is an error processing JSON
	 */

	@PostMapping("/registerPatient")
	public String submitPatientRegistration(@ModelAttribute("patient") Patient patient, Model model)
			throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl = "http://localhost:8084/registerPatient";
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

	/**
	 * Handles requests to find a patient by name.
	 * 
	 * @param name  the name of the patient to search for
	 * @param model the model to add attributes to
	 * @return the name of the patient search results page view
	 */
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
			return "patientList";
		} 
			return "patientSearch";
	}

	/**
	 * Handles requests to delete a patient by ID.
	 * 
	 * @param patientId the ID of the patient to delete
	 * @param model     the model to add attributes to
	 * @return the name of the status page view
	 */
	@RequestMapping(value = "/findPatientById", method = RequestMethod.GET)
	public String findPatientById(@RequestParam("patientId") int patientId, Model model) {
		Patient patient = null;
		String url = "http://localhost:8084/viewPatient/" + patientId;

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

	/**
	 * Handles GET requests to view a patient's profile.
	 * 
	 * This method retrieves the profile of a patient identified by the given
	 * patientId. It sends a GET request to the specified URL and adds the
	 * retrieved patient object to the model. If the patient is found, it
	 * returns the view name for the profile page. In case of an error, it
	 * adds an error message to the model and returns the same view name.
	 * 
	 * @param patientId the ID of the patient whose profile is to be viewed
	 * @param model     the model to add attributes to for the view
	 * @return the name of the view to display the patient's profile
	 */
	@GetMapping("/viewPatientProfile")
	public String viewProfile(@RequestParam int patientId, Model model) {
		Patient patient = null;
		String url = "http://localhost:8084/viewPatient/" + patientId;
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

	/**
	 * Handles GET requests to update a patient's information.
	 * 
	 * This method retrieves the patient details for the given patientId and
	 * prepares the model for the update page. If the patient is found, it
	 * adds the patient object to the model and returns the view name for the
	 * update page. If the patient is not found or an error occurs, it adds
	 * an appropriate error message to the model and returns the patient list view.
	 * 
	 * @param patientId the ID of the patient to be updated
	 * @param model     the model to add attributes to for the view
	 * @return the name of the view to display the update page or patient list
	 */
	@GetMapping("/updatePatient")
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
			return "updatePage";
		} else {
			model.addAttribute("errorMessage", "No Patient found with the given patientId : " + patientId);
			return "patientList";
		}
	}

	/**
	 * Handles POST requests to submit updates to a patient's information.
	 * 
	 * This method processes the submitted patient data and sends a PUT request
	 * to update the patient's information on the server. If the update is
	 * successful, it adds the updated patient object and a success message to
	 * the model. In case of an error during the update, it captures the error
	 * message from the response and adds it to the model, along with the
	 * original patient data for re-display on the update page.
	 * 
	 * @param patient the patient object containing updated information
	 * @param model   the model to add attributes to for the view
	 * @return the name of the view to display the update page with success or error
	 *         message
	 * @throws JsonMappingException    if there is an error mapping JSON to the
	 *                                 object
	 * @throws JsonProcessingException if there is an error processing JSON
	 */
	@PostMapping("/updatePatient")
	public String submitUpdatePatient(@ModelAttribute("patient") Patient patient, Model model)
			throws JsonMappingException, JsonProcessingException {
		Patient patientObj = null;
		String reuestUrl = "http://localhost:8084/updatePatient/" + patient.getPatientId();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);

		try {
			ResponseEntity<Patient> response = restTemplate.exchange(reuestUrl, HttpMethod.PUT, requestEntity,
					Patient.class);
			patientObj = response.getBody();
		} catch (HttpClientErrorException e) {

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

	/**
	 * Handles requests to deactivate a patient by their ID.
	 * 
	 * This method sends a PUT request to the server to deactivate a patient
	 * identified by the given patientId. If the operation is successful, it
	 * retrieves the updated list of all patients and displays it. In case of
	 * an error, it adds an error message to the model.
	 * 
	 * @param patientId the ID of the patient to deactivate
	 * @param model     the model to add attributes to
	 * @return the name of the view to display the updated patient list
	 */
	@GetMapping("/deactivatePatient")
	public String deactivatePatient(@RequestParam("patientId") int patientId, Model model) {
		Patient patient = null;
		String url = "http://localhost:8084/deactivatePatient/" + patientId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
		try {
			ResponseEntity<Patient> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Patient.class);
			patient = response.getBody();
			if(patient.isActive())
			model.addAttribute("successMessage", "Patient with Id (" + patientId + ") activated successfully.");
			else 
			model.addAttribute("errorMessage", "Patient with Id (" + patientId + ") deactivated successfully.");
			return getAllPatient(model);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage",
					"Unable to fetch Patient with Id (" + patientId + "). Please try again later.");
			return "patientList";
		}
	}

	/**
	 * Handles requests to view all patients.
	 * 
	 * This method sends a GET request to the server to retrieve a list of all
	 * patients. If successful, it adds the list to the model for display. If
	 * there are no patients or an error occurs, it adds an appropriate error
	 * message to the model.
	 * 
	 * @param model the model to add attributes to
	 * @return the name of the view to display the patient list
	 */
	@RequestMapping(value = "/viewAllPatient", method = RequestMethod.GET)
	public String getAllPatient(Model model) {
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

	/**
	 * Handles patient login requests.
	 * 
	 * This method processes the login form submission for patients. It validates
	 * the input, constructs a request to the server for authentication, and
	 * manages the session attributes based on the login result. If successful,
	 * it redirects to the patient page; otherwise, it returns to the home page
	 * with an error message.
	 * 
	 * @param username the username (patient ID) entered by the user
	 * @param password the password entered by the user
	 * @param model    the model to add attributes to
	 * @param session  the HTTP session to store user information
	 * @return the name of the view to redirect to
	 */
	@PostMapping("/patientLogin")
	public String patientLogin(@RequestParam String username, @RequestParam String password, Model model,
			HttpSession session) {
		if (username.isEmpty() || password.isEmpty()) {
			model.addAttribute("errorMessage", "Please enter Patient Id and Password.");
			return "homePage";
		}
		LoginDetails details = new LoginDetails(username, password, "patient");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<LoginDetails> requestEntity = new HttpEntity<>(details, headers);

		String requestUrl = "http://localhost:8084/login";

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
				System.out.println(errorMessage.get("error"));
				session.setAttribute("errorMessage", errorMessage.get("error"));
			} catch (Exception parseException) {
				System.out.println(parseException.getMessage());
				session.setAttribute("errorMessage", "An error occurred while parsing the validation errors.");
			}
		}
		return "redirect:/"; // Redirect back to the login page in case of failure
	}

}
