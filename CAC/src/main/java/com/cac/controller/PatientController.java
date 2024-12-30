package com.cac.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cac.model.Patient;
import com.cac.service.PatientService;

import jakarta.validation.Valid;

@RestController
public class PatientController {
	
	@Autowired
	private PatientService patientService;
	
	@PostMapping("/registerPatient") 
	public ResponseEntity<Patient> registerPatient(@Valid @RequestBody Patient patient){
		return new ResponseEntity<Patient>(patientService.createPatient(patient), HttpStatus.CREATED);
	}
	
	@PutMapping("/updatePatientName/{id}/{name}")
	public ResponseEntity<Patient> updatePatientName(@PathVariable int id, @PathVariable String name ) throws Exception{
		return new ResponseEntity<Patient>(patientService.updatePatientName(id,name), HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/updatePatient/{id}")
	public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody Patient patient) throws Exception{
		return new ResponseEntity<Patient>(patientService.updatePatient(id, patient), HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/deactivatePatient/{id}")
	public ResponseEntity<Patient> changePatientActive(@PathVariable int id) throws Exception{
		return new ResponseEntity<Patient>(patientService.changeActive(id), HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/viewPatient/{id}")
	public ResponseEntity<Patient> getPatientById(@PathVariable int id){
		Patient patient = patientService.getPatientById(id);
		return ResponseEntity.ok(patient);
	}
	
	@GetMapping("/viewPatientByName/{name}")
	public ResponseEntity<List<Patient>> getPatientById(@PathVariable String name) throws Exception{
		List<Patient> patientList = patientService.getPatientsByName(name);
		return ResponseEntity.ok(patientList);
	}
	
	@GetMapping("/viewAllPatient")
	public ResponseEntity<List<Patient>> getAllPatient(){
		List<Patient> patientList = patientService.getAllPatients();
		return new ResponseEntity<List<Patient>>(patientList, HttpStatus.OK);
	}
	
	@GetMapping("/viewAllActivePatient")
	public ResponseEntity<List<Patient>> getAllActivePatient(){
		List<Patient> patientList = patientService.getAllActivePatient();
		return new ResponseEntity<List<Patient>>(patientList, HttpStatus.OK);
	}
	
	@PostMapping("/patientLogin")
	public ResponseEntity<String> patientLogin(@RequestParam int username, @RequestParam String password) {
		Patient patient = patientService.getPatientById(username);
		if(patient==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found with given id : "+username);
		if(patient.isActive()==false) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not active. Please contact Hospital");
		if(patient.getPatientId()==username && patient.getPatientName().equals(password)) return ResponseEntity.ok("Patient Login Successfull");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials. Please try again.");
	}
	
	

}
