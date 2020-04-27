package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
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
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class AdminController implements Initializable, Controller {

  private App mainApp;

  @FXML Button btn_logout;
  @FXML Button btn_Accept;
  @FXML Button btn_Deny;
  @FXML ChoiceBox<Employee> cb_Employee;
  @FXML TableView<Request> tb_RequestTable = new TableView<Request>();
  @FXML TableView<String> tb_languages = new TableView<String>();
  @FXML CheckBox ch_requestFilter;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {

    try {
      populateRequestTable();
      populateChoiceBox();
      populateLanguageTable();
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    initializeTable();
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
    notes.setCellValueFactory(new PropertyValueFactory<Request, String>("notes"));

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
    language.setCellValueFactory(new PropertyValueFactory<Request, String>("language"));

    TableColumn<Request, String> service = new TableColumn<>("Service");
    service.setMaxWidth(75);
    service.setMinWidth(75);
    service.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceType"));

    // Language Table for Translators
    TableColumn<String, String> languages = new TableColumn<>("Languages");
    languages.setMaxWidth(150);
    languages.setMinWidth(150);
    languages.setCellValueFactory(new selfFactory<String>());

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
}
