package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class EditEmployeeController {

  @FXML JFXButton btn_addLang;
  @FXML JFXTextField txtf_empid;
  @FXML JFXTextField txtf_newLang;
  @FXML TableView tb_languagesRemove;

  NewAdminController newAdminController;

  public void updateTranslator(MouseEvent event) throws DBException {

    if (txtf_empid.getText().equals("")) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Needs user ID");
      errorAlert.show();

      return;
    }

    int empID = Integer.parseInt(txtf_empid.getText());

    try {
      if (event.getSource() == btn_addLang) {

        if (txtf_newLang.getText().equals("")) {
          Alert errorAlert = new Alert(Alert.AlertType.ERROR);
          errorAlert.setContentText("Needs Language");
          errorAlert.show();

          return;
        }

        ServiceDB.addLanguage(empID, txtf_newLang.getText());

        tb_languagesRemove.getItems().add(txtf_newLang.getText());

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText(ServiceDB.getEmployee(empID).getName() + " languages were added");
        acceptReq.show();
      }
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_empid.clear();
    txtf_newLang.clear();
  }

  public void setAdminController(NewAdminController newAdminController) {
    this.newAdminController = newAdminController;
  }
}
