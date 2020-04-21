package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class AdminController implements Initializable, Controller {

  private App mainApp;
  // public LoginController controller;
  int initalSetup = 0;

  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;
  @FXML Button btn_Accept;
  @FXML Button btn_Deny;
  @FXML TableView<MockData> tbMockData = new TableView<MockData>();
  @FXML TableColumn<MockData, String> currentData = new TableColumn<>("Data");

  // @FXML TableColumn<MockData, String> data;
  ObservableList<MockData> newData = FXCollections.observableArrayList();

  Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    if (App.adminDataStorage.newData != null) {
      TableColumn<MockData, Integer> requestID = new TableColumn<>("ID");
      TableColumn<MockData, Integer> emp_assigned = new TableColumn<>("Assigned");
      TableColumn<MockData, String> notes = new TableColumn<>("Notes");
      TableColumn<MockData, String> nodeID = new TableColumn<>("Location");
      TableColumn<MockData, GregorianCalendar> timeRequested = new TableColumn<>("Time Started");
      TableColumn<MockData, GregorianCalendar> timeCompleted = new TableColumn<>("Time Completed");
      TableColumn<MockData, String> status = new TableColumn<>("Status");
      TableColumn<MockData, String> language = new TableColumn<>("Language");

      requestID.setMinWidth(100);
      emp_assigned.setMinWidth(100);
      notes.setMinWidth(100);
      nodeID.setMinWidth(100);
      timeRequested.setMinWidth(100);
      timeCompleted.setMinWidth(100);
      status.setMinWidth(100);
      language.setMinWidth(100);

      requestID.setCellValueFactory(new PropertyValueFactory<MockData, Integer>("requestID"));
      emp_assigned.setCellValueFactory(new PropertyValueFactory<MockData, Integer>("emp_assigned"));
      notes.setCellValueFactory(new PropertyValueFactory<MockData, String>("notes"));
      nodeID.setCellValueFactory(new PropertyValueFactory<MockData, String>("nodeID"));
      timeRequested.setCellValueFactory(
          new PropertyValueFactory<MockData, GregorianCalendar>("timeRequested"));
      timeCompleted.setCellValueFactory(
          new PropertyValueFactory<MockData, GregorianCalendar>("timeCompleted"));
      status.setCellValueFactory(new PropertyValueFactory<MockData, String>("status"));
      language.setCellValueFactory(new PropertyValueFactory<MockData, String>("language"));

      newData.setAll(App.adminDataStorage.newData);
      tbMockData.setItems(newData);
      tbMockData
          .getColumns()
          .addAll(
              requestID,
              emp_assigned,
              notes,
              nodeID,
              timeRequested,
              timeCompleted,
              status,
              language);
    }
  }

  @FXML
  public void closeScreen(MouseEvent event) {
    App.adminDataStorage.newData.clear();
    App.adminDataStorage.newData.addAll(tbMockData.getItems());
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
  public void acceptRow(MouseEvent e) {
    if (e.getSource() == btn_Accept) {
      confirmationAlert.show();
      confirmationAlert.setContentText(
          "Accept " + tbMockData.getSelectionModel().getSelectedCells().toString());
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
    } else if (e.getSource() == btn_Deny) {
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
    }
  }
}
