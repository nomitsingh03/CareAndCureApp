package com.cac.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
public class Patient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int patientId;
	
	@Column(nullable = false)
	@NotBlank(message = "Patient Name is mandatory")
	private String patientName;

	@Positive(message = "Age must be required")
	private int age;
	private LocalDate dateOfBirth;

	@NotBlank(message="Select gender")
	private String gender;

	@NotBlank(message = "Contact Number is mandatory")
	@Column(nullable = false)
	@Pattern(
			regexp = "^\\+[1-9]{1}[0-9]{0,2}[0-9]{10}$",
			message = "Contact number must start with a country code (e.g., +1) followed by a 10-digit mobile number"
	)
	private String contactNumber;


	@Email(message = "Enter valid email", regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$")
	@Column(unique = true)
	private String emailId;
	private String medicalHistory;

	@NotBlank(message = "Address required!")
	private String address;
	private String allergies;
	private String medications;
	private String treatments;
	private String insuranceDetails;
	private boolean isActive;
	
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMedicalHistory() {
		return medicalHistory;
	}
	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAllergies() {
		return allergies;
	}
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}
	public String getMedications() {
		return medications;
	}
	public void setMedications(String medications) {
		this.medications = medications;
	}
	public String getTreatments() {
		return treatments;
	}
	public void setTreatments(String treatments) {
		this.treatments = treatments;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getInsuranceDetails() {
		return insuranceDetails;
	}
	public void setInsuranceDetails(String insuranceDetails) {
		this.insuranceDetails = insuranceDetails;
	}
	
	

}
