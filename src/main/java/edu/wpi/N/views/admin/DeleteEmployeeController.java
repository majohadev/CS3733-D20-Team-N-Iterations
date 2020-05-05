package edu.wpi.N.views.admin;

import edu.wpi.N.database.DBException;

public class DeleteEmployeeController {

  NewAdminController newAdminController;

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  public void deleteEmployee() throws DBException {
    newAdminController.deleteEmployee();
    newAdminController.populateTable();
  }
}
