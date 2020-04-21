package edu.wpi.N.views;

import edu.wpi.N.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    if (App.adminDataStorage.newData != null) {
      TableColumn<MockData, String> data = new TableColumn<>("Data");
      data.setMinWidth(100);
      data.setCellValueFactory(new PropertyValueFactory<MockData, String>("data"));
      newData.setAll(App.adminDataStorage.newData);
      tbMockData.setItems(newData);
      tbMockData.getColumns().addAll(data);
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
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
    } else if (e.getSource() == btn_Deny) {
      tbMockData.getItems().removeAll(tbMockData.getSelectionModel().getSelectedItem());
    }
  }
}
