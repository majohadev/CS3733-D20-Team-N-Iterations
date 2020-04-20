package edu.wpi.N.entities;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Employee {
  private int id;
  private String name;
  private int yearsofExperience;
  private char gender;
  private Timestamp start, end;
  private String type;

  public Employee(int id, String name, int date, String lang, char gender, Timestamp start, Timestamp end, String type) {
    this.id = id;
    this.name = name;
    this.yearsofExperience = date;
    this.gender = gender;
    this.start = start;
    this.end = end;
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getYearsofExperience() {
    return yearsofExperience;
  }

  public void setYearsofExperience(int yearsofExperience) {
    this.yearsofExperience = yearsofExperience;
  }

  public char getGender() {
    return gender;
  }

  public void setGender(char gender) {
    this.gender = gender;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Timestamp getStart() {
    return start;
  }

  public void setStart(Timestamp start) {
    this.start = start;
  }

  public Timestamp getEnd() {
    return end;
  }

  public void setEnd(Timestamp end) {
    this.end = end;
  }
}
