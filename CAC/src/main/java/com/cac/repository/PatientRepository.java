package com.cac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cac.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer>{
	
	List<Patient> findByPatientNameAllIgnoreCase(String name);
	
	List<Patient> findByIsActive(boolean active);

}
