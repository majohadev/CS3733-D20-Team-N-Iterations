package edu.wpi.N.controllerData;

import edu.wpi.N.views.AdminController;
import edu.wpi.N.views.MockData;
import java.util.ArrayList;

public class AdminDataStorage {

  public AdminDataStorage() {
    newData.add(new MockData(0, 0, "", "location", null, null, "", ""));
    newData.add(new MockData(10, 24, "", "location", null, null, "", ""));
  }

  public ArrayList<MockData> newData = new ArrayList<MockData>();

  public AdminController storeAdminController(AdminController controller) {
    AdminController newController = controller;
    return newController;
  }

  /*
  public ArrayList<MockData> addToList(ServiceRequest request){
    ArrayList<MockData> updatedData = new ArrayList<MockData>();
    updatedData.add(request);
    return updatedData;
  }
   */

}
