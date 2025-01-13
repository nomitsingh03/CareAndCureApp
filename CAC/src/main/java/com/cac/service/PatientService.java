package com.cac.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.Patient;
import com.cac.model.UserInfo;
import com.cac.repository.PatientRepository;

import jakarta.mail.MessagingException;

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
				"<html>" +
						"<body>" +
						"<h1>Welcome to Care & Cure</h1>" +
						"<p>Dear <b>%s</b>,</p>" +
						"<p>Thank you for registering with Care & Cure. Your patient ID is: <b>%d</b>.</p>" +
						"<h3>ABOUT CARE & CURE HOSPITAL</h3>" +
						"<p>Care & Cure is dedicated to improving health and well-being with compassionate, personalized healthcare services.</p>"
						+
						"<h3>OUR MISSION</h3>" +
						"<p>To provide accessible, high-quality medical care with a focus on excellence, patient-centered treatment, and innovation.</p>"
						+
						"<h3>OUR VISION</h3>" +
						"<p>To become a global leader in healthcare, known for trust, quality, and advancing medical research and education.</p>"
						+
						"<h3>OUR FACILITIES</h3>" +
						"<ul>" +
						"  <li>24/7 Emergency and Trauma Care</li>" +
						"  <li>State-of-the-art Diagnostic and Laboratory Services</li>" +
						"  <li>Advanced Surgical and Inpatient Facilities</li>" +
						"  <li>Specialized Clinics for Cardiology, Orthopedics, and more</li>" +
						"  <li>Preventive Health Programs and Wellness Services</li>" +
						"</ul>" +
						"<p>For any assistance or inquiries, feel free to reach out to us at: <a href='mailto:support@careandcure.com'>support@careandcure.com</a></p>"
						+
						"<p>Our team of highly skilled professionals is dedicated to providing you with world-class healthcare. We are honored to be part of your health journey.</p>"
						+
						"<p>Best regards,<br>Care & Cure Team</p>" +
						"</body>" +
						"</html>",
				savedPatient.getPatientName(), savedPatient.getPatientId());

		try {
			emailService.sendEmail(savedPatient.getEmailId(), subject, message);
		} catch (MessagingException | MailSendException e) {
			throw new MailSendException("Failed to send Message");
		}
	}

	public Patient updatePatientName(int id, String name) throws UserNotFoundException {
		Patient patient = patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));
		patient.setPatientName(name);
		return patientRepository.save(patient);
	}

	public Patient getPatientById(int id) throws UserNotFoundException {
		return patientRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Patient not found with Id: " + id));
	}

	public List<Patient> getAllPatients() {
		List<Patient> patientList = patientRepository.findAll();
		return patientList;
	}

	public List<Patient> getPatientsByName(String name) throws UserNotFoundException {
		List<Patient> patients = patientRepository.findByPatientNameContainingIgnoreCase(name);
		if (patients.isEmpty() || patients.size() == 0)
			throw new UserNotFoundException("Patients not found with name : " + name);
		return patients;
	}

	public List<Patient> getAllPatientByStatus(boolean flag) {
		return patientRepository.findByIsActive(flag);
	}

	public Patient updatePatient(int id, Patient patient) throws UserNotFoundException, MessagingException {
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
		if (!oldDetail.getGender().equals(patient.getGender())) {
			oldDetail.setGender(patient.getGender());
			updateDetails.append("- Gender details updated.\n");
			isUpdated = true;
		}
		if (!oldDetail.getDateOfBirth().equals(patient.getDateOfBirth())) {
			oldDetail.setDateOfBirth(patient.getDateOfBirth());
			updateDetails.append("- DOB details updated.\n");
			isUpdated = true;
		}

		if (isUpdated) {
			Patient updatedPatient = patientRepository.save(oldDetail);

			String subject = "Profile Update Notification - Care & Cure";
			String message = "<html><body>" +
					"<p>Dear <strong>" + updatedPatient.getPatientName() + "</strong>,</p>" +
					"<p>We hope this message finds you well.</p>" +
					"<p>This is to inform you that the following details in your Care & Cure profile have been successfully updated:</p>"
					+
					"<p><strong>" + updateDetails + "</strong></p>" +
					"<p>If you did not make these changes, please contact our support team immediately at <a href='mailto:support@careandcure.com'>support@careandcure.com</a> to secure your account.</p>"
					+
					"<p>Thank you for choosing Care & Cure. We are committed to providing you with the best healthcare experience.</p>"
					+
					"<p>Warm regards,<br>" +
					"The Care & Cure Team<br></p>" +
					"</body></html>";

			emailService.sendEmail(updatedPatient.getEmailId(), subject, message);
			return updatedPatient;
		} else {
			throw new IllegalArgumentException("No changes detected to update the profile.");
		}
	}

	public Patient changeActive(int id) throws UserNotFoundException, MessagingException {
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
					"You can now access all our healthcare services. If you need any assistance or have questions, please feel free to contact us.\n\n"
					+
					"Best regards,\n" +
					"The Care & Cure Team\n" +
					"Contact Us: support@careandcure.com\n" +
					"Phone: +1-800-555-CARE";

		} else {
			// Account Deactivation Email
			subject = "Your Care & Cure Account Has Been Deactivated";
			message = "Dear " + updatedPatient.getPatientName() + ",\n\n" +
					"We regret to inform you that your Care & Cure account has been deactivated. \n\n"
					+ "If you did not request this change, or if you wish to reactivate your account, please reach out to our support team.\n\n"
					+
					"We value your trust, and our team is here to assist you with any questions or concerns you may have.\n\n"
					+
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
