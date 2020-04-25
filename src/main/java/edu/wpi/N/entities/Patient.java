package edu.wpi.N.entities;

public class Patient {
  private int id;
  private String name;
  private String location;

  public Patient(int id, String name, String location) {
    this.id = id;
    this.name = name;
    this.location = location;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLocation() {
    return location;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Patient)) {
      return false;
    }

    Patient other = (Patient) obj;

    return id == other.id && name.equals(other.name) && location.equals(other.location);
  }
}
