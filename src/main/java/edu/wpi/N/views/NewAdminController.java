package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DoctorDB;
import edu.wpi.N.database.LoginDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class NewAdminController implements Controller, Initializable {

  private App mainApp = null;

  @FXML Label lbl_Time;
  @FXML Label lbl_Date;
  @FXML JFXPasswordField pwf_newpass;
  @FXML JFXPasswordField pwf_confpass;
  @FXML JFXTextField txtf_newuser;
  @FXML JFXTextField txtf_docuser;
  @FXML JFXTextField txtf_docpass;
  @FXML JFXTextField txtf_docfn;
  @FXML JFXTextField txtf_docln;
  @FXML JFXTextField txtf_docfield;
  @FXML JFXTextField txtf_empfn;
  @FXML JFXTextField txtf_empln;
  @FXML JFXTextField txtf_languages;
  @FXML JFXTextField txtf_adminuser;
  @FXML JFXTextField txtf_adminpass;
  @FXML JFXTextField txtf_cpoldpass;
  @FXML JFXTextField txtf_cpnewpass;
  @FXML JFXTextField txtf_cpuser;
  @FXML StackPane sp_getpanes;
  @FXML Pane pn_pane1;
  @FXML Pane pn_pane2;
  @FXML Pane pn_pane3;
  @FXML JFXButton btn_EmployeeEdit;
  @FXML JFXButton btn_AccountEdit;
  @FXML JFXButton btn_ViewRequests;
  @FXML JFXButton btn_EditMap;
  @FXML TableView<Employee> tbl_Employees;
  @FXML CheckBox cb_translator;
  @FXML Label lbl_languages;

  ObservableList<Employee> emps = FXCollections.observableArrayList();

  /*
  cb_Employee
       .valueProperty()
       .addListener(
           (ov, old, emp) -> {
             if (emp instanceof Translator) {
               languageData.setAll(((Translator) emp).getLanguages());
             } else {
               languageData.setAll(new LinkedList<String>());
             }
           });
  */

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      tableSetup();
      populateTable();

      cb_translator
          .selectedProperty()
          .addListener(
              (ob, old, newVal) -> {
                if (cb_translator.isSelected()) {
                  txtf_languages.setVisible(true);
                  lbl_languages.setVisible(true);
                } else {
                  txtf_languages.setVisible(false);
                  lbl_languages.setVisible(false);
                }
              });

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void tableSetup() {
    TableColumn<Employee, Integer> empID = new TableColumn<>("ID");
    empID.setMaxWidth(30);
    empID.setMinWidth(30);
    empID.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("ID"));

    TableColumn<Employee, String> name = new TableColumn<>("Name");
    name.setMaxWidth(200);
    name.setMinWidth(200);
    name.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));

    tbl_Employees.getColumns().addAll(empID, name);
  }

  public void populateTable() throws DBException {
    try {
      tbl_Employees.getItems().clear();
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      emps.addAll(empList);
      tbl_Employees.setItems(emps);

      tbl_Employees
          .getSelectionModel()
          .selectedItemProperty()
          .addListener(
              (obs, old, newVal) -> {
                try {
                  emps.setAll(ServiceDB.getEmployees());
                } catch (DBException e) {
                  e.printStackTrace();
                }
              });

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  /*
  public void setTime() throws InterruptedException {
    try {
      Thread.sleep(60 * 1000); // One Minute

      LocalTime hour = null;
      LocalTime minute = null;
      lbl_Time.setText(hour.getHour() + ":" + minute.getMinute());

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
   */

  @FXML
  public void addNewAdmin() throws DBException {
    try {
      String newUser = txtf_newuser.getText();
      String newPass = pwf_newpass.getText();
      System.out.println(
          "Original Pass: " + pwf_newpass.getText() + " Conf Pass: " + pwf_confpass.getText());
      LoginDB.createAdminLogin(newUser, newPass);
      System.out.println("Admin Created");

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void changePanes(MouseEvent e) {
    if (e.getSource() == btn_AccountEdit) {
      System.out.println("In Account Edit");
      pn_pane1.setVisible(true);
      pn_pane2.setVisible(false);
      pn_pane3.setVisible(false);
    } else if (e.getSource() == btn_EmployeeEdit) {
      System.out.println("In Employee Edit");
      pn_pane2.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane3.setVisible(false);
    } else if (e.getSource() == btn_ViewRequests) {
      System.out.println("In View Requests");
      pn_pane3.setVisible(true);
      pn_pane1.setVisible(false);
      pn_pane2.setVisible(false);
    }
  }

  public void addDoc() throws DBException {
    String fullName = (txtf_docfn.getText() + " " + txtf_docln.getText());
    try {
      DoctorDB.addDoctor(
          fullName, txtf_docfield.getText(), txtf_docuser.getText(), txtf_docpass.getText(), null);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  /*
   Removes an employee from the database
  */

  public void deleteEmployee() throws DBException {
    try {
      ServiceDB.removeEmployee(tbl_Employees.getSelectionModel().getSelectedItem().getID());
      int removeLine = tbl_Employees.getSelectionModel().getSelectedIndex();
      tbl_Employees.getItems().remove(removeLine);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  /*
  REFACTOR TO SUPPORT ALL NEW EMPLOYEES
   */
  public void addEmployee() throws DBException {
    String name = txtf_empfn.getText() + " " + txtf_empln.getText();
    try {
      if (cb_translator.isSelected()) {
        String[] arrOfString = txtf_languages.getText().split(",");
        LinkedList<String> languages = new LinkedList<>();
        for (String a : arrOfString) {
          languages.add(a);
        }
        tbl_Employees
            .getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (obs, old, newVal) -> {
                  try {
                    emps.setAll(ServiceDB.getEmployees());
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                });
        // else if() (Sanatation Condition)
        ServiceDB.addTranslator(name, languages);
      } else {
        ServiceDB.addLaundry(name);

        tbl_Employees
            .getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (obs, old, newVal) -> {
                  try {
                    emps.setAll(ServiceDB.getEmployees());
                  } catch (DBException e) {
                    e.printStackTrace();
                  }
                });
      }
      populateTable();
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void adminEditMap() throws IOException {
    mainApp.switchScene("views/mapEdit.fxml");
  }

  public void returnToPrev() throws IOException {
    mainApp.switchScene("views/home.fxml");
  }

  public void addAdmin() throws DBException {
    try {
      LoginDB.createAdminLogin(txtf_adminuser.getText(), txtf_adminpass.getText());
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void changeAccountPass() throws DBException {
    try {
      if (txtf_cpnewpass.getText().equals(txtf_cpoldpass.getText()))
        throw new DBException("New password same as old password");
      LoginDB.changePass(txtf_cpuser.getText(), txtf_cpoldpass.getText(), txtf_cpnewpass.getText());
      System.out.println("Password Updated");
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void logoutUser() throws DBException {
    try {
      LoginDB.logout();
      mainApp.switchScene("views/newLogin.fxml");
    } catch (DBException | IOException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }
}
