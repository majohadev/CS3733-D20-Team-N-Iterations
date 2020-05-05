package edu.wpi.N.views.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.employees.Translator;
import edu.wpi.N.entities.request.Request;
import edu.wpi.N.views.Controller;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class ViewRequestController implements Controller, Initializable {

  private App mainApp = null;
  private StateSingleton singleton;

  @FXML TableView<Request> tb_RequestTable = new TableView<Request>();
  @FXML TableView<String> tb_languages = new TableView<String>();
  @FXML JFXButton btn_Accept;
  @FXML JFXButton btn_Deny;
  @FXML JFXCheckBox ch_requestFilter;
  @FXML ChoiceBox<Employee> cb_Employee;

  ObservableList<Request> tableData = FXCollections.observableArrayList();
  ObservableList<String> languageData = FXCollections.observableArrayList();

  @Override
  public void setMainApp(App mainApp) {}

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    try {
      populateChoiceBox();
      populateRequestTable();
      populateLanguageTable();
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    initializeTable();
  }

  public static class nodeLongName
      implements Callback<TableColumn.CellDataFeatures<Request, String>, ObservableValue<String>> {

    public nodeLongName() {}

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Request, String> param) {
      try {
        DbNode node = MapDB.getNode(param.getValue().getNodeID());
        if (node == null) {
          return new ReadOnlyObjectWrapper<>("Invalid Location");
        }
        return new ReadOnlyObjectWrapper<>(node.getLongName());
      } catch (DBException e) {
        return new ReadOnlyObjectWrapper<>("Invalid Location");
      }
    }
  }

  private static class selfFactory<G>
      implements Callback<TableColumn.CellDataFeatures<G, G>, ObservableValue<G>> {
    public selfFactory() {}

    @Override
    public ObservableValue<G> call(TableColumn.CellDataFeatures<G, G> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    }
  }

  /** Initializes a table with all given parameters for service requests */
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
    notes.setCellValueFactory(new PropertyValueFactory<Request, String>("reqNotes"));

    TableColumn<Request, String> nodeID = new TableColumn<>("Location");
    nodeID.setMaxWidth(100);
    nodeID.setMinWidth(100);
    nodeID.setCellValueFactory(new ViewRequestController.nodeLongName());

    TableColumn<Request, String> status = new TableColumn<>("Status");
    status.setMaxWidth(100);
    status.setMinWidth(100);
    status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));

    TableColumn<Request, String> attr1 = new TableColumn<>("Attribute 1");
    attr1.setMaxWidth(100);
    attr1.setMinWidth(100);
    attr1.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr1"));

    TableColumn<Request, String> service = new TableColumn<>("Service");
    service.setMaxWidth(75);
    service.setMinWidth(75);
    service.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceType"));

    TableColumn<Request, String> attr2 = new TableColumn<>("Attribute 2");
    attr2.setMaxWidth(100);
    attr2.setMinWidth(100);
    attr2.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr2"));

    TableColumn<Request, String> attr3 = new TableColumn<>("Attribute 3");
    attr3.setMaxWidth(100);
    attr3.setMinWidth(100);
    attr3.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr3"));

    TableColumn<Request, String> attr4 = new TableColumn<>("Attribute 4");
    attr4.setMaxWidth(100);
    attr4.setMinWidth(100);
    attr4.setCellValueFactory(new PropertyValueFactory<Request, String>("Atr4"));

    // Language Table for Translators
    TableColumn<String, String> languages = new TableColumn<>("Languages");
    languages.setMaxWidth(150);
    languages.setMinWidth(150);
    languages.setCellValueFactory(new ViewRequestController.selfFactory<String>());

    // Initializes Columns for Language Table (Filling Requests) and Request Table (Viewing,
    // Accepting, Denying)
    tb_RequestTable
        .getColumns()
        .addAll(
            requestID, service, emp_assigned, notes, nodeID, status, attr1, attr2, attr3, attr4);

    tb_languages.getColumns().addAll(languages);
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
      } else if (e.getSource() == btn_Deny) {
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

  private void assignEmployeeToRequest(int employee, int ID) throws DBException {
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
    populateRequestTable();
  }

  @FXML
  private void assignPressed(MouseEvent e) throws DBException {
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

  public void populateChoiceBox() throws DBException {
    try {
      LinkedList<Employee> empList = ServiceDB.getEmployees();
      ObservableList<Employee> empObv = FXCollections.observableArrayList();
      empObv.addAll(empList);
      cb_Employee.setItems(empObv);
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
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
}
