package edu.wpi.N.entities;

import java.util.LinkedList;

public class Doctor {
  private int id;
  private String name;
  private LinkedList<DbNode> loc;
  private String field;

  public Doctor(int id, String name, LinkedList<DbNode> loc, String field) {
    this.id = id;
    this.name = name;
    this.loc = loc;
    this.field = field;
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

  public LinkedList<DbNode> getLoc() {
    return loc;
  }

  public void setLoc(LinkedList<DbNode> loc) {
    this.loc = loc;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Doctor)) {
      return false;
    }

    Doctor other = (Doctor) o;

    return this.name.equals(other.name)
        && this.loc.equals(other.loc)
        && this.field.equals(other.field);
  }
}
