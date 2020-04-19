package edu.wpi.N.entities;

import java.sql.Time;
import java.util.Date;

public class Employee {
    private int id;
    private String name;
    private int yearsofExperience;
    private String lang;
    private char gender;
    private Time available;

    public Employee(int id, String name, int date, String lang, char gender, Time available){
        this.id = id;
        this.name=name;
        this.yearsofExperience = date;
        this.lang = lang;
        this.gender = gender;
        this.available = available;
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

    public int getYearsofExperience() {
        return yearsofExperience;
    }

    public void setYearsofExperience(int yearsofExperience) {
        this.yearsofExperience = yearsofExperience;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Time getAvailable() {
        return available;
    }

    public void setAvailable(Time available) {
        this.available = available;
    }
}
