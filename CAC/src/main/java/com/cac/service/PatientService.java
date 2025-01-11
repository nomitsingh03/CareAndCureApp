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

	@Autowired
	private EmailService emailService;

	public Patient createPatient(Patient patient) {
		// Activate the new patient
		patient.setActive(true);
		// Save patient information to the repository
		Patient savedPatient = patientRepository.save(patient);

		// Create user information for login or account management
		UserInfo userInfo = new UserInfo(String.valueOf(savedPatient.getPatientId()),
				patient.getPatientName(),
				"patient");
		userService.createUser(userInfo);

		// Send a detailed welcome email
		sendWelcomeEmail(savedPatient);

		return savedPatient;
	}
	private void sendWelcomeEmail(Patient savedPatient) {
		String subject = "Welcome to Care & Cure";
		String message = String.format(
				"Dear %s,\n\n" +
						"Thank you for registering with Care & Cure. Your patient ID is: %d.\n\n" +
						"ABOUT CARE & CURE HOSPITAL\n" +
						"Care & Cure is dedicated to improving health and well-being with compassionate, personalized healthcare services.\n\n" +
						"OUR MISSION\n" +
						"To provide accessible, high-quality medical care with a focus on excellence, patient-centered treatment, and innovation.\n\n" +
						"OUR VISION\n" +
						"To become a global leader in healthcare, known for trust, quality, and advancing medical research and education.\n\n" +
						"OUR FACILITIES\n" +
						"- 24/7 Emergency and Trauma Care\n" +
						"- State-of-the-art Diagnostic and Laboratory Services\n" +
						"- Advanced Surgical and Inpatient Facilities\n" +
						"- Specialized Clinics for Cardiology, Orthopedics, and more\n" +
						"- Preventive Health Programs and Wellness Services\n\n" +
						"For any assistance or inquiries, feel free to reach out to us at: support@careandcure.com\n\n" +
						"Our team of highly skilled professionals is dedicated to providing you with world-class healthcare. We are honored to be part of your health journey.\n\n" +
						"Best regards,\n" +
						"Care & Cure Team",
				savedPatient.getPatientName(), savedPatient.getPatientId()
		);

		emailService.sendEmail(savedPatient.getEmailId(), subject, message);
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
		Patient oldDetail = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));

		boolean isUpdated = false;
		StringBuilder updateDetails = new StringBuilder();

		if (!oldDetail.getPatientName().equals(patient.getPatientName())) {
			oldDetail.setPatientName(patient.getPatientName());
			updateDetails.append("- Name updated to: ").append(patient.getPatientName()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getEmailId().equals(patient.getEmailId())) {
			oldDetail.setEmailId(patient.getEmailId());
			updateDetails.append("- Email updated to: ").append(patient.getEmailId()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getAllergies().equals(patient.getAllergies())) {
			oldDetail.setAllergies(patient.getAllergies());
			updateDetails.append("- Allergies updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getContactNumber().equals(patient.getContactNumber())) {
			oldDetail.setContactNumber(patient.getContactNumber());
			updateDetails.append("- Contact number updated to: ").append(patient.getContactNumber()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getMedicalHistory().equals(patient.getMedicalHistory())) {
			oldDetail.setMedicalHistory(patient.getMedicalHistory());
			updateDetails.append("- Medical history updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getTreatments().equals(patient.getTreatments())) {
			oldDetail.setTreatments(patient.getTreatments());
			updateDetails.append("- Treatments updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getMedications().equals(patient.getMedications())) {
			oldDetail.setMedications(patient.getMedications());
			updateDetails.append("- Medications updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getAddress().equals(patient.getAddress())) {
			oldDetail.setAddress(patient.getAddress());
			updateDetails.append("- Address updated.\n");
			isUpdated = true;
		}
		if (oldDetail.getAge() != patient.getAge()) {
			oldDetail.setAge(patient.getAge());
			updateDetails.append("- Age updated to: ").append(patient.getAge()).append("\n");
			isUpdated = true;
		}
		if (!oldDetail.getInsuranceDetails().equals(patient.getInsuranceDetails())) {
			oldDetail.setInsuranceDetails(patient.getInsuranceDetails());
			updateDetails.append("- Insurance details updated.\n");
			isUpdated = true;
		}

		if (isUpdated) {
			Patient updatedPatient = patientRepository.save(oldDetail);

			String subject = "Profile Update Notification - Care & Cure";
			String message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We hope this message finds you well.\n\n" +
					"This is to inform you that the following details in your Care & Cure profile have been successfully updated:\n\n" +
					updateDetails +
					"\nIf you did not make these changes, please contact our support team immediately at support@careandcure.com to secure your account.\n\n" +
					"Thank you for choosing Care & Cure. We are committed to providing you with the best healthcare experience.\n\n" +
					"Warm regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";

			emailService.sendEmail(updatedPatient.getEmailId(), subject, message);
			return updatedPatient;
		}
		else {
			throw new IllegalArgumentException("No changes detected to update the profile.");
		}
	}


	public Patient changeActive(int id) throws UserNotFoundException {
		Patient patient = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));

		// Toggle active status
		patient.setActive(!patient.isActive());
		Patient updatedPatient = patientRepository.save(patient);

		String subject;
		String message;

		if (updatedPatient.isActive()) {
			// Account Activation Email
			subject = "Your Care & Cure Account Has Been Activated";
			message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We are pleased to inform you that your Care & Cure account has been successfully activated.\n\n" +
					"You can now access all our healthcare services. If you need any assistance or have questions, please feel free to contact us.\n\n" +
					"Best regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";

		} else {
			// Account Deactivation Email
			subject = "Your Care & Cure Account Has Been Deactivated";
			message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We regret to inform you that your Care & Cure account has been deactivated. \n\n" + "If you did not request this change, or if you wish to reactivate your account, please reach out to our support team.\n\n" +
					"We value your trust, and our team is here to assist you with any questions or concerns you may have.\n\n" +
					"Best regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";
		}

		// Send the respective email
		emailService.sendEmail(updatedPatient.getEmailId(), subject, message);

		return updatedPatient;
	}



}
