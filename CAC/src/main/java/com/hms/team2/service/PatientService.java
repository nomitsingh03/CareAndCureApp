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
		patient.setActive(true);
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

		oldDetail.setPatientName(patient.getPatientName());
		oldDetail.setEmailId(patient.getEmailId());
		oldDetail.setAllergies(patient.getAllergies());
		oldDetail.setContactNumber(patient.getContactNumber());
		oldDetail.setMedicalHistory(patient.getMedicalHistory());
		oldDetail.setTreatments(patient.getTreatments());
		oldDetail.setMedications(patient.getMedications());
		oldDetail.setAddress(patient.getAddress());
		oldDetail.setDateOfBirth(oldDetail.getDateOfBirth());
//		oldDetail.setAge(Period.between(oldDetail.getDateOfBirth(), LocalDate.now()).getYears());
		oldDetail.setAge(patient.getAge());
		return patientRepository.save(oldDetail);
	}

	public Patient changeActive(int id) {
		// TODO Auto-generated method stub
		Patient patient = patientRepository.findById(id).orElse(null);
		System.out.println(patient.isActive());
		if(patient==null) return null;
		patient.setActive(!patient.isActive());
		System.out.println(patient.isActive());
		return patientRepository.save(patient);
	}

}
