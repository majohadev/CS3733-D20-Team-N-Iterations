package edu.wpi.N.views.admin;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.*;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.entities.employees.Employee;
import edu.wpi.N.entities.request.Request;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewAdminController implements Controller, Initializable {

  private App mainApp = null;

  private StateSingleton singleton;

  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @FXML JFXButton btn_EmployeeEdit;
  @FXML JFXButton btn_AccountEdit;
  @FXML JFXButton btn_ViewRequests;
  @FXML JFXButton btn_EditMap;
  @FXML Label lbl_title;
  @FXML AnchorPane anchorSwap;
  @FXML JFXButton btn_arduino;
  @FXML TableView tb_RequestTable;
  @FXML JFXCheckBox ch_requestFilter;

  ObservableList<Request> tableData = FXCollections.observableArrayList();

  // inject singleton
  public NewAdminController(StateSingleton singleton) {
    this.singleton = singleton;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      setTitleLabel();
      populateRequestTable();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
    initializeTable();
  }

  /** Pops up a new window with File Manager */
  @FXML
  private void popUpFileManager() {
    try {
      Stage stage = new Stage();
      Parent root;

      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("fileManagementScreen.fxml"));

      loader.setControllerFactory(
          type -> {
            try {
              // Inject singleton into DataEditorController
              return new DataEditorController(singleton);
            } catch (Exception exc) {
              exc.printStackTrace();
              throw new RuntimeException(exc);
            }
          });

      root = loader.load();

      Controller controller = loader.getController();
      controller.setMainApp(mainApp);

      Scene scene = new Scene(root);
      stage.setScene(scene);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.show();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Oops... Something went Wong");
      errorAlert.setContentText("Error when openning File Manager Window");
      errorAlert.showAndWait();
    }
  }

  public void adminEditMap() throws IOException {
    mainApp.switchScene("views/mapEditor/mapEditor.fxml", singleton);
  }

  public void logoutUser() throws DBException {
    try {
      LoginDB.logout();
      mainApp.switchScene("views/admin/newLogin.fxml", singleton);
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

  public void editMap() throws IOException {
    mainApp.switchScene("views/mapEditor/mapEdit.fxml", singleton);
  }

  public void setTitleLabel() throws DBException {
    try {
      lbl_title.setText("Welcome, " + LoginDB.currentLogin());
    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
  }

  @FXML
  public void loadArduino() {
    Stage stage = new Stage();
    Parent root = null;
    try {
      root = FXMLLoader.load(getClass().getResource("edu/wpi/N/views/admin/arduinoInterface.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene scene = new Scene(root);
    stage.setScene(scene);
    // stage.initModality(Modality.APPLICATION_MODAL);
    stage.show();
  }

  @FXML
  public void swapToAccount() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("accountHandeler.fxml"));
    anchorSwap.getChildren().setAll(currentPane);
  }

  @FXML
  public void swapToEmployee() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("employeeHandler.fxml"));
    anchorSwap.getChildren().setAll(currentPane);
  }

  @FXML
  public void swapToRequests() throws IOException {
    AnchorPane currentPane = FXMLLoader.load(getClass().getResource("viewRequest.fxml"));
    anchorSwap.getChildren().setAll(currentPane);
  }

  public void populateRequestTable() throws DBException {
    LinkedList<Request> reqs = ServiceDB.getRequests();
    tableData.setAll(reqs);

    /*
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
     */

    tb_RequestTable.setItems(tableData);
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

    tb_RequestTable
        .getColumns()
        .addAll(
            requestID, service, emp_assigned, notes, nodeID, status, attr1, attr2, attr3, attr4);
  }
}
