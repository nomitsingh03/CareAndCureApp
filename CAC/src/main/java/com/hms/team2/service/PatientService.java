package com.hms.team2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.team2.model.Patient;
import com.hms.team2.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	public PatientRepository patientRepository;
	
	public Patient createPatient(Patient patient) {
		return patientRepository.save(patient);
	}
	
	public Patient updatePatientName(int id, String name) {
		Patient patient = patientRepository.findById(id).orElse(null);
		if(patient==null) return null;
		patient.setPatientName(name);
		return patientRepository.save(patient);
	}
	
	public Patient getPatientById(int id) {
		return patientRepository.findById(id).orElse(null);
	}

	public List<Patient> getAllPatients() {
		return patientRepository.findAll();
	}
	
	public List<Patient> getPatientsByName(String name){
		return patientRepository.findByPatientNameAllIgnoreCase(name);
	}
	
	public List<Patient> getAllActivePatient(){
		return patientRepository.findByIsActive(true);
	}
	
	public Patient updatePatient(int id, Patient patient) {
		Patient oldDetail = patientRepository.findById(id).orElse(null);
		if(oldDetail==null) return null;
		return patientRepository.save(patient);
	}

	public Patient changeActive(int id) {
		// TODO Auto-generated method stub
		Patient patient = patientRepository.findById(id).orElse(null);
		if(patient==null) return null;
		patient.setActive(false);
		return patientRepository.save(patient);
	}

}
