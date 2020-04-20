package edu.wpi.N.entities;
import java.util.GregorianCalendar;

public abstract class Request {
    int requestID;
    int emp_assigned;
    String notes;
    String nodeID;
    String serviceType;
    GregorianCalendar timeRequested;
    GregorianCalendar timeCompleted;
    String status;
    Request(int requestID, int emp_assigned, String notes, String nodeID, String serviceType, GregorianCalendar timeRequested, GregorianCalendar timeCompleted, String status){
        this.requestID = requestID;
        this.emp_assigned = emp_assigned;
        this.notes = notes;
        this.nodeID = nodeID;
        this.serviceType = serviceType;
        this.timeCompleted = timeCompleted;
        this.timeRequested = timeRequested;
        this.status = status;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getEmp_assigned() {
        return emp_assigned;
    }

    public String getNotes() {
        return notes;
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getServiceType() {
        return serviceType;
    }

    public GregorianCalendar getTimeRequested() {
        return timeRequested;
    }

    public GregorianCalendar getTimeCompleted() {
        return timeCompleted;
    }
}
