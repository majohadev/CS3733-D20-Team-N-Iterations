package edu.wpi.N.views.admin;

import edu.wpi.N.database.DBException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DeleteEmployeeController {

  NewAdminController newAdminController;

  @FXML Label lbl_empname;
  @FXML Label lbl_empid;

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  public void deleteEmployee() throws DBException {
    newAdminController.deleteEmployee();
    newAdminController.populateTable();
  }
}
