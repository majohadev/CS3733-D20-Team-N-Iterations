package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXTextArea;
import edu.wpi.N.database.DBException;
import javafx.fxml.FXML;

public class DenyRequestController {

  NewAdminController newAdminController;

  @FXML JFXTextArea txtf_compNotes;

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  @FXML
  public void denyRequest() throws DBException {
    String compNotes = txtf_compNotes.getText();
    newAdminController.denyRow(compNotes);
    newAdminController.populateRequestTable();
    txtf_compNotes.clear();
  }
}
