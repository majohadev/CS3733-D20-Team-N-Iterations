package edu.wpi.N.entities;

import java.sql.Time;

public class Translator extends Employee {
  private String lang;

  public Translator(int id, String name, int date, char gender, Time available, String lang) {
    super(id, name, date, gender, available);
    this.lang = lang;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }
}
