package edu.wpi.N.entities;

import java.sql.Time;

public class Laundry extends Employee {
  public Laundry(int id, String name, int date, String lang, char gender, Time available) {
    super(id, name, date, null, gender, available);
  }
}
