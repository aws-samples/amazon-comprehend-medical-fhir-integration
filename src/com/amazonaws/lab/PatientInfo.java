package com.amazonaws.lab;

import java.util.ArrayList;


public class PatientInfo {
	
	private ArrayList<String> notes;
	private String firstName;
	private String lastName;
	private String middleInitial;
	private String dateOfBirth;
	private String activityDateTime;
	private String addressCity;
	private String gender;
	private String MRN;
	private String patientId;
	
	
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getMRN() {
		return MRN;
	}
	public void setMRN(String mRN) {
		MRN = mRN;
	}
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getActivityDateTime() {
		return activityDateTime;
	}
	public void setActivityDateTime(String activityDateTime) {
		this.activityDateTime = activityDateTime;
	}
	public ArrayList<String> getNotes() {
		return notes;
	}
	public void setNotes(ArrayList<String> notes) {
		this.notes = notes;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleInitial() {
		return middleInitial;
	}
	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	

}
