package edu.wpi.N.entities.request;

import edu.wpi.N.entities.Flower;

import java.util.GregorianCalendar;
import java.util.LinkedList;


public class FlowerRequest extends Request {

    private String patientName, visitorName, creditNum;
    private String flowerList;

    public FlowerRequest(int requestID, int emp_assigned, String reqNotes, String compNotes, String nodeID, GregorianCalendar timeRequested, GregorianCalendar timeCompleted, String status, String patient, String visitor, String num, LinkedList<Flower> list) {
        super(requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
        this.patientName = patient;
        this.visitorName = visitor;
        this.creditNum = num;
        LinkedList<String> stringFlower = new LinkedList<>();
        for(int i = 0; i < list.size(); i++){
            stringFlower.add(list.get(i).getFlowerName());
        }
        String sample = stringFlower.toString();
        this.flowerList = sample.substring(1, sample.length()-1);
    }

    @Override
    public String getServiceType() {
        return "Flower";
    }

    public String getPatientName() {
        return patientName;
    }

    public String getVisitorName() {
        return visitorName;
    }


    public String getCreditNum() {
        return creditNum;
    }

    public String getFlowerList() {
        return flowerList;
    }

    public void setFlowerList(String flowerList) {
        this.flowerList = flowerList;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlowerRequest)) {
            return false;
        }

        FlowerRequest other = (FlowerRequest) o;

        return super.equals(o)
                && patientName.equals(other.patientName)
                && visitorName.equals(other.visitorName)
                && creditNum.equals(other.creditNum)
                && flowerList.equals(other.flowerList);
    }
}
