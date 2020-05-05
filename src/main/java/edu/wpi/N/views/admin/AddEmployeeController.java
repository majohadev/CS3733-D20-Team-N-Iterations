package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.Service;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;

public class AddEmployeeController implements Initializable, Controller {

  NewAdminController newAdminController;

  @FXML JFXTextField txtf_empfn;
  @FXML JFXTextField txtf_empln;
  @FXML JFXTextField txtf_languages;
  @FXML ChoiceBox<Service> cb_employeeTypes;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      populateEmployeeType();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  public void setAdminController(NewAdminController adminController) {
    this.newAdminController = adminController;
  }

  public void addEmployee() throws DBException {
    String name = txtf_empfn.getText() + " " + txtf_empln.getText();
    try {
      if (txtf_empfn.getText().equals("")) throw new DBException("Employee has no first name");

      switch (cb_employeeTypes.getValue().getServiceType()) {
        case "Laundry":
          {
            ServiceDB.addLaundry(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Wheelchair":
          {
            ServiceDB.addWheelchairEmployee(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "IT":
          {
            ServiceDB.addIT(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Emotional Support":
          {
            ServiceDB.addEmotionalSupporter(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Sanitation":
          {
            ServiceDB.addSanitationEmp(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Flower":
          {
            ServiceDB.addFlowerDeliverer(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Internal Transportation":
          {
            ServiceDB.addInternalTransportationEmployee(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Security":
          {
            ServiceDB.addSecurityOfficer(name);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
        case "Translator":
          {
            String[] arrOfString = txtf_languages.getText().split(",");
            LinkedList<String> languages = new LinkedList<>();
            for (String a : arrOfString) {
              languages.add(a);
            }
            ServiceDB.addTranslator(name, languages);

            Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
            acceptReq.setContentText("Employee " + name + " was added.");
            acceptReq.show();
            break;
          }
      }

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }

    txtf_empfn.clear();
    txtf_empln.clear();
    txtf_languages.clear();
    newAdminController.populateTable();
    // employeeHandleController.populateChoiceBox();
  }

  public void populateEmployeeType() throws DBException {
    LinkedList<Service> employeeList = new LinkedList<Service>();
    ObservableList<Service> empTypeList = FXCollections.observableArrayList();

    for (Service services : ServiceDB.getServices()) {
      if (!services.getServiceType().equals("Medicine")) {
        employeeList.add(services);
      }
    }

    empTypeList.addAll(employeeList);
    cb_employeeTypes.setItems(empTypeList);
  }

  @Override
  public void setMainApp(App mainApp) {}
}
