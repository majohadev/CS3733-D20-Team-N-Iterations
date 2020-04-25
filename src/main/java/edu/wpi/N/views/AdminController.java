package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
import java.io.IOException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class AdminController implements Initializable, Controller {

  private App mainApp;
  private HomeController homeController;
  // public LoginController controller;
  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;
  @FXML Button btn_Accept;
  @FXML Button btn_Deny;
  @FXML Button btn_Assign;
  @FXML ChoiceBox<Employee> cb_Employee;
  @FXML TableView<Request> tbMockData = new TableView<Request>();
  @FXML TableView<String> tb_languages = new TableView<String>();
  @FXML CheckBox ch_requestFilter;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();

  Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

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
    if (App.adminDataStorage.newData != null) {
      try {
        LinkedList<Request> reqs = ServiceDB.getRequests();
        tableData.setAll(reqs);
      } catch (DBException e) {
        Alert newAlert = new Alert(Alert.AlertType.ERROR);
        newAlert.setContentText(e.getMessage());
        newAlert.show();
      }

      TableColumn<Request, Integer> requestID = new TableColumn<>("ID");
      TableColumn<Request, Employee> emp_assigned = new TableColumn<>("Assigned");
      TableColumn<Request, String> notes = new TableColumn<>("Notes");
      TableColumn<Request, String> nodeID = new TableColumn<>("Location");
      TableColumn<Request, GregorianCalendar> timeRequested = new TableColumn<>("Time Started");
      TableColumn<Request, GregorianCalendar> timeCompleted = new TableColumn<>("Time Completed");
      TableColumn<Request, String> status = new TableColumn<>("Status");
      TableColumn<Request, String> language = new TableColumn<>("Language");
      TableColumn<Request, String> service = new TableColumn<>("Service");

      TableColumn<String, String> languages = new TableColumn<>("Languages");
      languages.setMinWidth(150);
      language.setMaxWidth(150);
      languages.setCellValueFactory(new selfFactory<String>());

      requestID.setMinWidth(20);
      emp_assigned.setMinWidth(100);
      notes.setMinWidth(75);
      nodeID.setMinWidth(100);
      // timeRequested.setMinWidth(100);
      // timeCompleted.setMinWidth(100);
      status.setMinWidth(100);
      language.setMinWidth(100);

      requestID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("requestID"));
      emp_assigned.setCellValueFactory(new PropertyValueFactory<Request, Employee>("emp_assigned"));
      notes.setCellValueFactory(new PropertyValueFactory<Request, String>("notes"));
      nodeID.setCellValueFactory(new PropertyValueFactory<Request, String>("nodeID"));
      timeRequested.setCellValueFactory(
          new PropertyValueFactory<Request, GregorianCalendar>("timeRequested"));
      timeCompleted.setCellValueFactory(
          new PropertyValueFactory<Request, GregorianCalendar>("timeCompleted"));
      status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
      language.setCellValueFactory(new PropertyValueFactory<Request, String>("language"));
      service.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceType"));

      tbMockData.setItems(tableData);
      tbMockData
          .getColumns()
          .addAll(requestID, service, emp_assigned, notes, nodeID, status, language);
      tb_languages.setItems(languageData);
      tb_languages.getColumns().addAll(languages);
      try {
        populateChoiceBox();
      } catch (DBException e) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setContentText(e.getMessage());
        errorAlert.show();
      }
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

    } else {
      System.out.println("is null");
    }
  }

  @FXML
  public void closeScreen(MouseEvent event) {
    ((Node) (event.getSource())).getScene().getWindow().hide();
  }

  @FXML
  public void editMap(MouseEvent e) throws IOException {
    this.mainApp.switchScene("editMap.fxml");
    // ((Node) (e.getSource())).getScene().getWindow().hide();
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
                      tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned()
              != null)) {
        ServiceDB.completeRequest(
            tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID(), null);

        Alert acceptReq = new Alert(Alert.AlertType.CONFIRMATION);
        acceptReq.setContentText("Request Accepted");
        acceptReq.show();

        if (ch_requestFilter.isSelected()) {
          tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Deny) { // This case needs a status check
        ServiceDB.denyRequest(
            tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID(), null);

        Alert denyReq = new Alert(Alert.AlertType.WARNING);
        denyReq.setContentText("Request Denied");
        denyReq.show();

        if (ch_requestFilter.isSelected()) {
          tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
        } else {
          LinkedList<Request> reqs = ServiceDB.getRequests();
          tableData.setAll(reqs);
        }
      } else if (e.getSource() == btn_Accept
          && (ServiceDB.getRequest(
                      tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID())
                  .getEmp_assigned())
              == null) {
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

  @FXML
  private void selectEmp() {
    Employee emp = cb_Employee.getSelectionModel().getSelectedItem();
    if (emp instanceof Translator) {
      languageData.setAll(((Translator) emp).getLanguages());
    } else {
      languageData.removeAll();
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
      rID = tbMockData.getSelectionModel().getSelectedItem().getRequestID();
    } catch (NullPointerException indx) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a request and an employee!");
      errorAlert.show();
      return;
    }
    assignEmployeeToRequest(eID, rID);

    try {
      if (ch_requestFilter.isSelected()) {
        LinkedList<Request> reqs = ServiceDB.getOpenRequests();
        tableData.setAll(reqs);
      } else {
        LinkedList<Request> reqs = ServiceDB.getRequests();
        tableData.setAll(reqs);
      }

    } catch (DBException ev) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(ev.getMessage());
      errorAlert.show();
    }
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
