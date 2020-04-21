package edu.wpi.N.entities;

import java.util.LinkedList;

public class Translator extends Employee {
  private LinkedList<String> languages;

  public Translator(int id, String name, LinkedList<String> langs) {
    super(id, name);
    this.languages = langs;
  }

  public LinkedList<String> getLanguages() {
    return languages;
  }

  @Override
  public String getServiceType() {
    return "Translator";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Translator)) {
      return false;
    }

    Translator other = (Translator) o;

    return getID() == other.getID()
        && getName().equals(other.getName())
        && languages.equals(other.languages);
  }
}
