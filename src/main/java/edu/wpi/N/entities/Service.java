package edu.wpi.N.entities;

abstract class Service {

    DbNode node;
    String notes;

    public Service(DbNode node, String notes){
        this.node = node;
        this.notes = notes;
    }

    public DbNode getNode() {
        return node;
    }

    public void setNode(DbNode node) {
        this.node = node;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
