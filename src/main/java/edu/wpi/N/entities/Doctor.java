package edu.wpi.N.entities;

public class Doctor {
    private String DoctorID;
    private String name;
    private DbNode loc;
    private String field;


    public String getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(String doctorID) {
        DoctorID = doctorID;
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
}
