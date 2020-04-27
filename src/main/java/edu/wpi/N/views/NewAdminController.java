package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class NewAdminController implements Controller, Initializable {

  private App mainApp = null;

  @FXML Label lbl_Time;
  @FXML Label lbl_Date;
  @FXML JFXPasswordField pwf_newpass;
  @FXML JFXPasswordField pwf_confpass;
  @FXML JFXTextField txtf_docoffices;
  @FXML JFXTextField txtf_newuser;
  @FXML JFXTextField txtf_docuser;
  @FXML JFXTextField txtf_docpass;
  @FXML JFXTextField txtf_docfn;
  @FXML JFXTextField txtf_docln;
  @FXML JFXTextField txtf_docfield;
  @FXML JFXTextField txtf_empfn;
  @FXML JFXTextField txtf_empln;
  @FXML JFXTextField txtf_languages;
  @FXML JFXTextField txtf_docid;
  @FXML JFXTextField txtf_adminuser;
  @FXML JFXTextField txtf_adminpass;
  @FXML JFXTextField txtf_cpoldpass;
  @FXML JFXTextField txtf_cpnewpass;
  @FXML JFXTextField txtf_cpuser;
  @FXML JFXTextField txtf_empid;
  @FXML JFXTextField txtf_newLang;
  @FXML StackPane sp_getpanes;
  @FXML Pane pn_pane1;
  @FXML Pane pn_pane2;
  @FXML Pane pn_pane3;
  @FXML JFXButton btn_EmployeeEdit;
  @FXML JFXButton btn_AccountEdit;
  @FXML JFXButton btn_ViewRequests;
  @FXML JFXButton btn_EditMap;
  @FXML JFXButton btn_addLanguage;
  @FXML JFXButton btn_removeLanguage;
  @FXML TableView<Employee> tbl_Employees;
  @FXML CheckBox cb_translator;
  @FXML Label lbl_languages;
  @FXML JFXListView lst_docoffice;
  @FXML Button btn_Accept;
  @FXML Button btn_Deny;
  @FXML ChoiceBox<Employee> cb_Employee;
  @FXML TableView<Request> tb_RequestTable = new TableView<Request>();
  @FXML TableView<String> tb_languages = new TableView<String>();
  @FXML CheckBox ch_requestFilter;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchDoctorOffice = new LinkedList<>();
  private ObservableList<String> fuzzySearchTextListDoctorOffices =
      FXCollections.observableArrayList();
  private LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();
  LinkedList<String> longNamesList = new LinkedList<>(); // Stores Floor Node names
  LinkedList<DbNode> allFloorNodes; // stores all the nodes on the floor
  ObservableList<Employee> emps = FXCollections.observableArrayList();

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {

      pn_pane1.setVisible(false);
      pn_pane2.setVisible(false);
      pn_pane3.setVisible(false);

      populateRequestTable();
      populateChoiceBox();
      populateLanguageTable();
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

      allFloorNodes = MapDB.visNodes(4, "Faulkner");
      for (DbNode node : allFloorNodes) {
        longNamesList.add(node.getLongName());
      }

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    initializeTable();
  }

  public void tableSetup() {
    TableColumn<Employee, Integer> empID = new TableColumn<>("ID");
    empID.setMaxWidth(100);
    empID.setMinWidth(100);
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
    } catch (DBException er) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(er.getMessage());
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

  public void addOffice() throws DBException {

    System.out.println(txtf_docid.getText());

    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();
    DbNode addOfficeNode = fuzzySearchNodeList.get(currentSelection);
    int doctorID = Integer.parseInt(txtf_docid.getText());
    System.out.println("Doctor ID Parsed: " + doctorID);
    try {
      DoctorDB.addOffice(doctorID, addOfficeNode);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void fuzzySearchDoctorsOffices(KeyEvent keyInput) throws DBException {
    String currentText = txtf_docoffices.getText();
    fuzzySearchDoctorOffice = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchDoctorOffice != null) {

      for (DbNode node : fuzzySearchDoctorOffice) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListDoctorOffices = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListDoctorOffices = FXCollections.observableList(longNamesList);
    lst_docoffice.setItems(fuzzySearchTextListDoctorOffices);
  }

  @FXML
  private void searchByLocationTextFill(KeyEvent inputMethodEvent) throws DBException {
    String currentText = txtf_docoffices.getText();
    fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
    LinkedList<String> fuzzySearchStringList = new LinkedList<>();
    if (fuzzySearchNodeList != null) {

      for (DbNode node : fuzzySearchNodeList) {
        fuzzySearchStringList.add(node.getLongName());
      }

      fuzzySearchTextListDoctorOffices = FXCollections.observableList(fuzzySearchStringList);
    } else fuzzySearchTextListDoctorOffices = FXCollections.observableList(longNamesList);
    lst_docoffice.setItems(fuzzySearchTextListDoctorOffices);
  }

  public void removeOffice() throws DBException {
    System.out.println(txtf_docid.getText());

    int currentSelection = lst_docoffice.getSelectionModel().getSelectedIndex();
    DbNode removeOfficeNode = fuzzySearchNodeList.get(currentSelection);
    int doctorID = Integer.parseInt(txtf_docid.getText());
    System.out.println("Doctor ID Parsed: " + doctorID);

    try {
      for (DbNode node : DoctorDB.getDoctor(doctorID).getLoc()) {
        System.out.println(removeOfficeNode.getNodeID());
        System.out.println(node.getNodeID());
        if (node.getNodeID().equals(removeOfficeNode.getNodeID())) {
          DoctorDB.removeOffice(doctorID, removeOfficeNode);
        }
      }
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void updateTranslator(MouseEvent event) throws DBException {
    int empID = Integer.parseInt(txtf_empid.getText());
    try {
      if (event.getSource() == btn_addLanguage) {
        ServiceDB.addLanguage(empID, txtf_newLang.getText());
      } else if (event.getSource() == btn_removeLanguage) {
        ServiceDB.removeLanguage(empID, txtf_newLang.getText());
      }
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  private void initializeTable() {

    // Request Table
    TableColumn<Request, Integer> requestID = new TableColumn<>("ID");
    requestID.setMaxWidth(30);
    requestID.setMinWidth(30);
    requestID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("requestID"));

    TableColumn<Request, Employee> emp_assigned = new TableColumn<>("Assigned");
    emp_assigned.setMaxWidth(100);
    emp_assigned.setMinWidth(100);
    emp_assigned.setCellValueFactory(new PropertyValueFactory<Request, Employee>("emp_assigned"));

    TableColumn<Request, String> notes = new TableColumn<>("Notes");
    notes.setMaxWidth(150);
    notes.setMinWidth(150);
    notes.setCellValueFactory(new PropertyValueFactory<Request, String>("NOTES"));

    TableColumn<Request, String> nodeID = new TableColumn<>("Location");
    nodeID.setMaxWidth(100);
    nodeID.setMinWidth(100);
    nodeID.setCellValueFactory(new PropertyValueFactory<Request, String>("nodeID"));

    TableColumn<Request, String> status = new TableColumn<>("Status");
    status.setMaxWidth(100);
    status.setMinWidth(100);
    status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));

    TableColumn<Request, String> language = new TableColumn<>("Language");
    language.setMaxWidth(100);
    language.setMinWidth(100);
    language.setCellValueFactory(new PropertyValueFactory<Request, String>("LANGUAGE"));

    TableColumn<Request, String> service = new TableColumn<>("Service");
    service.setMaxWidth(75);
    service.setMinWidth(75);
    service.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceType"));

    // Language Table for Translators
    TableColumn<String, String> languages = new TableColumn<>("Languages");
    languages.setMaxWidth(150);
    languages.setMinWidth(150);
    languages.setCellValueFactory(new NewAdminController.selfFactory<String>());

    // Initializes Columns
    tb_RequestTable
        .getColumns()
        .addAll(requestID, service, emp_assigned, notes, nodeID, status, language);
    tb_languages.getColumns().addAll(languages);
  }

  public void populateRequestTable() throws DBException {
    LinkedList<Request> reqs = ServiceDB.getRequests();
    tableData.setAll(reqs);

    ch_requestFilter
        .selectedProperty()
        .addListener(
            (ov, old, val) -> {
              try {
                if (val) {
                  LinkedList<Request> rqs = ServiceDB.getOpenRequests();
                  tableData.setAll(rqs);
                } else {
                  LinkedList<Request> rqs = ServiceDB.getRequests();
                  tableData.setAll(rqs);
                }
              } catch (DBException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText(e.getMessage());
                errorAlert.show();
              }
            });
    tb_RequestTable.setItems(tableData);
  }

  public void populateLanguageTable() {
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
    tb_languages.setItems(languageData);
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  private void acceptRow(MouseEvent e) {
    try {
      if (e.getSource() == btn_Accept
          && (ServiceDB.getRequest(
                      tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned()
              != null)) {
        ServiceDB.completeRequest(
            tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID(), "");

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Request Accepted");
        acceptReq.show();

        if (ch_requestFilter.isSelected()) {
          tb_RequestTable
              .getItems()
              .removeAll(tb_RequestTable.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Deny) { // This case needs a status check
        ServiceDB.denyRequest(
            tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID(), "");
        ServiceDB.denyRequest(
            tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID(), "");

        Alert denyReq = new Alert(Alert.AlertType.WARNING);
        denyReq.setContentText("Request Denied");
        denyReq.show();

        if (ch_requestFilter.isSelected()) {
          tb_RequestTable
              .getItems()
              .removeAll(tb_RequestTable.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Accept
          && (ServiceDB.getRequest(
                      tb_RequestTable.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned()
              == null)) {
        Alert needEmp = new Alert(Alert.AlertType.ERROR);
        needEmp.setContentText("Needs an Assigned Employee");
        needEmp.show();
      }
    } catch (DBException ex) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(ex.getMessage());
      errorAlert.show();
    }
  }

  private void assignEmployeeToRequest(int employee, int ID) {
    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    try {
      ServiceDB.assignToRequest(employee, ID);
      confAlert.setContentText(
          cb_Employee.getSelectionModel().getSelectedItem().getName()
              + " was assigned to the request");
      confAlert.show();
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  @FXML
  private void assignPressed(MouseEvent e) {
    int eID;
    int rID;
    try {
      eID = cb_Employee.getSelectionModel().getSelectedItem().getID();
      rID = tb_RequestTable.getSelectionModel().getSelectedItem().getRequestID();
    } catch (NullPointerException indx) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a request and an employee!");
      errorAlert.show();
      return;
    }
    assignEmployeeToRequest(eID, rID);
  }

  public void populateChoiceBox() throws DBException {
    try {
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      ObservableList<Employee> empObv = FXCollections.observableArrayList(empList);
      cb_Employee.setItems(empObv);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  public void editMap() throws IOException {
    mainApp.switchScene("views/mapEdit.fxml");
  }
}
