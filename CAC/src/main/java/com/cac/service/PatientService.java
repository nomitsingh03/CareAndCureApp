package com.cac.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.PatientNotFound;
import com.cac.model.Patient;
import com.cac.repository.PatientRepository;

@Service
public class PatientService {
	
	@Autowired
	public PatientRepository patientRepository;
	
	public Patient createPatient(Patient patient) {
		patient.setActive(true);
		return patientRepository.save(patient);
	}
	
	public Patient updatePatientName(int id, String name) throws PatientNotFound {
		Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFound("Patient not found with Id: "+id));
		// if(patient==null) return null;
		patient.setPatientName(name);
		return patientRepository.save(patient);
	}
	
	public Patient getPatientById(int id) {
		return patientRepository.findById(id).orElse(null);
	}

	public List<Patient> getAllPatients() {
		List<Patient> patientList = patientRepository.findAll();
		return patientList;
	}
	
	public List<Patient> getPatientsByName(String name) throws PatientNotFound{
		List<Patient> patients = patientRepository.findByPatientNameAllIgnoreCase(name);
		if(patients.isEmpty() || patients.size()==0) throw new PatientNotFound("Patients not found with name : "+name);
		return patients;
	}
	
	public List<Patient> getAllActivePatient(){
		return patientRepository.findByIsActive(true);
	}
	
	public Patient updatePatient(int id, Patient patient) throws PatientNotFound {
		Patient oldDetail = patientRepository.findById(id).orElseThrow(()->new PatientNotFound("Patient not found with Id: "+id));

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

	public Patient changeActive(int id) throws PatientNotFound {
		// TODO Auto-generated method stub
		Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFound("Patient not found with Id: "+id));
		System.out.println(patient.isActive());
		//if(patient==null) return null;
		patient.setActive(!patient.isActive());
		System.out.println(patient.isActive());
		return patientRepository.save(patient);
	}

}
