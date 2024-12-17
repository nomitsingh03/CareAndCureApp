package com.hms.team2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.team2.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer>{
	
	List<Patient> findByPatientNameAllIgnoreCase(String name);
	
	List<Patient> findByIsActive(boolean active);

}
