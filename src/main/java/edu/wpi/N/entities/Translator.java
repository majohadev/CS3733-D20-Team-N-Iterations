package edu.wpi.N.entities;

import java.sql.Time;

public class Translator extends Employee {
  public Translator(int id, String name, int date, String lang, char gender, Time available) {
    super(id, name, date, lang, gender, available);
  }
}
