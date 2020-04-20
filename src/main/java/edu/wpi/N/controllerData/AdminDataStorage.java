package edu.wpi.N.controllerData;

import edu.wpi.N.views.AdminController;
import edu.wpi.N.views.MockData;
import java.util.ArrayList;

public class AdminDataStorage {

  public AdminDataStorage() {
    newData.add(new MockData("Hello"));
    newData.add(new MockData("Goodbye"));
  }

  public ArrayList<MockData> newData = new ArrayList<MockData>();

  public AdminController storeAdminController(AdminController controller) {
    AdminController newController = controller;
    return newController;
  }
}
