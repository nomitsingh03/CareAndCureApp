package com.hms.team2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hms.team2.model.Patient;
import com.hms.team2.service.PatientService;

@RestController
public class PatientController {
	
	@Autowired
	private PatientService patientService;
	
	@PostMapping("/registerPatient") 
	public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient){
		return new ResponseEntity<Patient>(patientService.createPatient(patient), HttpStatus.CREATED);
	}
	
	@PutMapping("/updatePatientName/{id}/{name}")
	public ResponseEntity<Patient> updatePatientName(@PathVariable int id, @PathVariable String name ){
		return new ResponseEntity<Patient>(patientService.updatePatientName(id,name), HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/updatePatient/{id}")
	public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody Patient patient){
		return new ResponseEntity<Patient>(patientService.updatePatient(id, patient), HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/deactivatePatient/{id}")
	public ResponseEntity<Patient> changePatientActive(@PathVariable int id){
		return new ResponseEntity<Patient>(patientService.changeActive(id), HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/viewPatient/{id}")
	public ResponseEntity<Patient> getPatientById(@PathVariable int id){
		Patient patient = patientService.getPatientById(id);
		return ResponseEntity.ok(patient);
	}
	
	
	@GetMapping("/viewPatientByName/{name}")
	public ResponseEntity<List<Patient>> getPatientById(@PathVariable String name){
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
	

}
