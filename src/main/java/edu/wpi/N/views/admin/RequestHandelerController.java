package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;

public class RequestHandelerController implements Initializable {

  @FXML JFXTextField txtf_compNotes;
  @FXML ChoiceBox<Employee> cb_Employees;

  NewAdminController newAdminController;

  @FXML JFXButton btn_Accept;
  @FXML JFXButton btn_Deny;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      populateChoiceBox();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the state of the admin controller so we can manipulate it from this controller
   *
   * @param adminContoller
   */
  public void setAdminController(NewAdminController adminContoller) {
    this.newAdminController = adminContoller;
  }

  public void populateChoiceBox() throws DBException {
    try {
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      ObservableList<Employee> empObv = FXCollections.observableArrayList();

      for (Employee emp : empList) {
        if (!emp.getServiceType().equals("Medicine")) {
          empObv.add(emp);
        }
      }
      cb_Employees.setItems(empObv);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void assignEmployee() throws DBException {
    newAdminController.assignEmployeeToRequest(cb_Employees.getSelectionModel().getSelectedItem());
  }

  public void acceptRequest() throws DBException {
    newAdminController.acceptRow();
  }
}
