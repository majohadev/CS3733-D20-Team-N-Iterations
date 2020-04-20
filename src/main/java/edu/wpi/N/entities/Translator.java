package edu.wpi.N.entities;

public class Translator extends Employee {
  private String language;

  public Translator(int id, String name, String lang) {
    super(id, name);
    this.language = lang;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
