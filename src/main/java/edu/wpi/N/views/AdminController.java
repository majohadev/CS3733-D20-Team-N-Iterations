package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.EmployeeController;
import edu.wpi.N.entities.Employee;
import edu.wpi.N.entities.Request;
import edu.wpi.N.entities.Translator;
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
import lombok.SneakyThrows;

public class AdminController implements Initializable, Controller {

  private App mainApp;
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

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    if (App.adminDataStorage.newData != null) {
      LinkedList<Request> reqs = EmployeeController.getOpenRequests();
      tableData.setAll(reqs);

      TableColumn<Request, Integer> requestID = new TableColumn<>("ID");
      TableColumn<Request, Employee> emp_assigned = new TableColumn<>("Assigned");
      TableColumn<Request, String> notes = new TableColumn<>("Notes");
      TableColumn<Request, String> nodeID = new TableColumn<>("Location");
      TableColumn<Request, GregorianCalendar> timeRequested = new TableColumn<>("Time Started");
      TableColumn<Request, GregorianCalendar> timeCompleted = new TableColumn<>("Time Completed");
      TableColumn<Request, String> status = new TableColumn<>("Status");
      TableColumn<Request, String> language = new TableColumn<>("Language");

      TableColumn<String, String> languages = new TableColumn<>("Languages");
      languages.setMinWidth(100);
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

      tbMockData.setItems(tableData);
      tbMockData.getColumns().addAll(requestID, emp_assigned, notes, nodeID, status, language);
      tb_languages.setItems(languageData);
      tb_languages.getColumns().addAll(languages);
      populateChoiceBox();
      cb_Employee
          .valueProperty()
          .addListener(
              (ov, old, emp) -> {
                if (emp instanceof Translator) {
                  languageData.setAll(((Translator) emp).getLanguages());
                } else {
                  languageData.removeAll();
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
    this.mainApp.switchScene("mapEdit.fxml");
    ((Node) (e.getSource())).getScene().getWindow().hide();
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  private void acceptRow(MouseEvent e) throws DBException {
    if (e.getSource() == btn_Accept) {
      EmployeeController.completeRequest(
          tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID());
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
    } else if (e.getSource() == btn_Deny) {
      EmployeeController.denyRequest(
          tbMockData.getSelectionModel().getSelectedItems().get(0).getRequestID());
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
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
    try {
      EmployeeController.assignToRequest(employee, ID);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
    }
  }

  @FXML
  private void assignPressed(MouseEvent e) {
    int eID = cb_Employee.getSelectionModel().getSelectedItem().getID();
    int rID = tbMockData.getSelectionModel().getSelectedItem().getRequestID();
    assignEmployeeToRequest(eID, rID);
    try {
      LinkedList<Request> reqs = EmployeeController.getOpenRequests();
      tableData.setAll(reqs);
    } catch (DBException ev) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(ev.getMessage());
    }
  }

  public void populateChoiceBox() throws DBException {
    LinkedList<Employee> empList = EmployeeController.getEmployees();
    ObservableList<Employee> empObv = FXCollections.observableArrayList(empList);
    cb_Employee.setItems(empObv);
  }
}
