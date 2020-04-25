package edu.wpi.N.entities;

public class Patient {
	private String name;
	private String location;

	public Patient(String name, String location){
		this.name = name;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}
}
