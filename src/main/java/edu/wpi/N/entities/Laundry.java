package edu.wpi.N.entities;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Laundry extends Employee {
  public Laundry(int id, String name, int date, String lang, char gender, Timestamp start, Timestamp end, String type) {
    super(id, name, date, null, gender, start, end, "Laundry");
  }
}
