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

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Patient)){
			return false;
		}

		Patient other = (Patient) obj;

		return name.equals(other.name) && location.equals(other.location);
	}
}
