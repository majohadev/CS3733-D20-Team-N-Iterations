package edu.wpi.N.entities;

import java.util.GregorianCalendar;

public class LaundryRequest extends Request{
    public LaundryRequest(int requestID, int emp_assigned, String notes, String nodeID, String serviceType, GregorianCalendar timeRequested, GregorianCalendar timeCompleted){
        super(requestID,  emp_assigned,  notes, nodeID, serviceType, timeRequested, timeCompleted);
    }
}
