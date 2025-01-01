package com.cac.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.Patient;
import com.cac.model.UserInfo;
import com.cac.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	public PatientRepository patientRepository;

	@Autowired
	private UserService userService;
	
	public Patient createPatient(Patient patient) {
		patient.setActive(true);
		Patient savedPatient = patientRepository.save(patient);
		UserInfo userInfo = new UserInfo(""+savedPatient.getPatientId(), patient.getPatientName(), "patient");
		userService.createUser(userInfo);
		return savedPatient;
	}
	
	public Patient updatePatientName(int id, String name) throws UserNotFoundException {
		Patient patient = patientRepository.findById(id).orElseThrow(()->new UserNotFoundException("Patient not found with Id: "+id));
		patient.setPatientName(name);
		return patientRepository.save(patient);
	}
	
	public Patient getPatientById(int id) throws UserNotFoundException {
		return patientRepository.findById(id).orElseThrow(()->new UserNotFoundException("Patient not found with Id: "+id));
	}

	public List<Patient> getAllPatients() {
		List<Patient> patientList = patientRepository.findAll();
		return patientList;
	}
	
	public List<Patient> getPatientsByName(String name) throws UserNotFoundException{
		List<Patient> patients = patientRepository.findByPatientNameAllIgnoreCase(name);
		if(patients.isEmpty() || patients.size()==0) throw new UserNotFoundException("Patients not found with name : "+name);
		return patients;
	}
	
	public List<Patient> getAllActivePatient(){
		return patientRepository.findByIsActive(true);
	}
	
	public Patient updatePatient(int id, Patient patient) throws UserNotFoundException {
		Patient oldDetail = patientRepository.findById(id).orElseThrow(()->new UserNotFoundException("Patient not found with Id: "+id));
		oldDetail.setPatientName(patient.getPatientName());
		oldDetail.setEmailId(patient.getEmailId());
		oldDetail.setAllergies(patient.getAllergies());
		oldDetail.setContactNumber(patient.getContactNumber());
		oldDetail.setMedicalHistory(patient.getMedicalHistory());
		oldDetail.setTreatments(patient.getTreatments());
		oldDetail.setMedications(patient.getMedications());
		oldDetail.setAddress(patient.getAddress());
		oldDetail.setDateOfBirth(oldDetail.getDateOfBirth());
		oldDetail.setAge(patient.getAge());
		return patientRepository.save(oldDetail);
	}

	public Patient changeActive(int id) throws UserNotFoundException {
		// TODO Auto-generated method stub
		Patient patient = patientRepository.findById(id).orElseThrow(()->new UserNotFoundException("Patient not found with Id: "+id));
		System.out.println(patient.isActive());
		//if(patient==null) return null;
		patient.setActive(!patient.isActive());
		return patientRepository.save(patient);
	}

}
