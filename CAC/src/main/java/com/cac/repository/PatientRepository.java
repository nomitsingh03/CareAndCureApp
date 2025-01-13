package com.cac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cac.model.Patient;

@SuppressWarnings("unused")
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer>{
	
	List<Patient> findByPatientNameContainingIgnoreCase(String name);
	
	List<Patient> findByIsActive(boolean active);

}
