package edu.wpi.N.views;

import edu.wpi.N.App;
import edu.wpi.N.entities.Request;
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

  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;
  @FXML Button btn_Accept;
  @FXML Button btn_Deny;
  @FXML TableView<Request> tbMockData = new TableView<Request>();
  @FXML TableColumn<Request, String> currentData = new TableColumn<>("Data");

  ObservableList<Request> tableData = FXCollections.observableArrayList();

  Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    if (App.adminDataStorage.newData != null) {
      TableColumn<Request, Integer> requestID = new TableColumn<>("ID");
      TableColumn<Request, Integer> emp_assigned = new TableColumn<>("Assigned");
      TableColumn<Request, String> notes = new TableColumn<>("Notes");
      TableColumn<Request, String> nodeID = new TableColumn<>("Location");
      TableColumn<Request, GregorianCalendar> timeRequested = new TableColumn<>("Time Started");
      TableColumn<Request, GregorianCalendar> timeCompleted = new TableColumn<>("Time Completed");
      TableColumn<Request, String> status = new TableColumn<>("Status");
      TableColumn<Request, String> language = new TableColumn<>("Language");

      requestID.setMinWidth(20);
      emp_assigned.setMinWidth(100);
      notes.setMinWidth(75);
      nodeID.setMinWidth(100);
      timeRequested.setMinWidth(100);
      timeCompleted.setMinWidth(100);
      status.setMinWidth(100);
      language.setMinWidth(100);

      requestID.setCellValueFactory(new PropertyValueFactory<Request, Integer>("requestID"));
      emp_assigned.setCellValueFactory(new PropertyValueFactory<Request, Integer>("emp_assigned"));
      notes.setCellValueFactory(new PropertyValueFactory<Request, String>("notes"));
      nodeID.setCellValueFactory(new PropertyValueFactory<Request, String>("nodeID"));
      timeRequested.setCellValueFactory(
          new PropertyValueFactory<Request, GregorianCalendar>("timeRequested"));
      timeCompleted.setCellValueFactory(
          new PropertyValueFactory<Request, GregorianCalendar>("timeCompleted"));
      status.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
      language.setCellValueFactory(new PropertyValueFactory<Request, String>("language"));

      tableData.setAll(App.adminDataStorage.newData);
      tbMockData.setItems(tableData);
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
    } else {
      System.out.println("is null");
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
