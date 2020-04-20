package edu.wpi.N.entities;

import java.sql.Timestamp;

public class Translator extends Employee {
  public Translator(
      int id,
      String name,
      int date,
      String lang,
      char gender,
      Timestamp start,
      Timestamp end,
      String type) {
    super(id, name, date, lang, gender, start, end, "Translator");
  }
}
