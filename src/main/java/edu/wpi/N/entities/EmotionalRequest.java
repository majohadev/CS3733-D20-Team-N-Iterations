package edu.wpi.N.entities;

import java.util.GregorianCalendar;

public class EmotionalRequest extends Request {
    public EmotionalRequest(
            int requestID,
            int emp_assigned,
            String notes,
            String nodeID,
            GregorianCalendar timeRequested,
            GregorianCalendar timeCompleted,
            String status) {
        super(requestID, emp_assigned, notes, nodeID, timeRequested, timeCompleted, status);
    }

    @Override
    public String getServiceType() {
        return "Emotional Support";
    }

    @Override
    public String getLanguage() {
        return "N/A";
    }
}
