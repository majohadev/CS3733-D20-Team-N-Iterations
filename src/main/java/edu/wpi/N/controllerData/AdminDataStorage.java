package edu.wpi.N.controllerData;

import edu.wpi.N.entities.Request;
import edu.wpi.N.views.AdminController;
import java.util.ArrayList;

public class AdminDataStorage {

  public ArrayList<Request> newData = new ArrayList<Request>();

  public AdminController storeAdminController(AdminController controller) {
    AdminController newController = controller;
    return newController;
  }

  public ArrayList<Request> getNewData() {
    System.out.println(newData.get(0).getRequestID());
    return newData;
  }

  public void setNewData(ArrayList<Request> newData) {
    this.newData = newData;
  }

  public void addToList(Request request) {
    newData.add(request);
    System.out.println("Request Obtained");
    System.out.println(newData.get(0).getRequestID());
  }
}
