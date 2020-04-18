package edu.wpi.N.entities;

public class Doctor {
  private String name;
  private DbNode loc;
  private String field;

  public Doctor(String name, DbNode loc, String field){
    this.name = name;
    this.loc = loc;
    this.field = field;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DbNode getLoc() {
    return loc;
  }

  public void setLoc(DbNode loc) {
    this.loc = loc;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public boolean equals(Object o){
    if(!(o instanceof Doctor)){
      return false;
    }

    Doctor other = (Doctor)o;

    return this.name.equals(other.name) &&
            this.loc.equals(other.loc) &&
            this.field.equals(other.field);
  }
}
