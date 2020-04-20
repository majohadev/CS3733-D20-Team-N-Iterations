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
  int initalSetup = 0;

  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;
  @FXML Button btn_Accept;
  @FXML TableView<MockData> tbMockData = new TableView<MockData>();
  @FXML TableColumn<MockData, String> currentData = new TableColumn<>("Data");

  // @FXML TableColumn<MockData, String> data;
  ObservableList<MockData> newData = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    if (initalSetup == 0) {
      TableColumn<MockData, String> data = new TableColumn<>("Data");
      data.setMinWidth(100);
      data.setCellValueFactory(new PropertyValueFactory<MockData, String>("data"));
      tbMockData.setItems(getMockDataFunc());
      tbMockData.getColumns().addAll(data);
      System.out.println(initalSetup);
      initalSetup = 1;
    } else if (initalSetup == 1) {
      TableColumn<MockData, String> updatedData = new TableColumn<>("Data");
      updatedData.setMinWidth(100);
      updatedData.setCellValueFactory(new PropertyValueFactory<MockData, String>("data"));
      tbMockData.setItems(newData);
      tbMockData.getColumns().addAll(updatedData);
      System.out.println(initalSetup);
    }
  }

  // Gets the initial list to populate the table (in final implementation this method will most
  // likely not exist)
  public ObservableList<MockData> getMockDataFunc() {
    ObservableList<MockData> data = FXCollections.observableArrayList();
    data.add(new MockData("Hello"));
    data.add(new MockData("Goodbye"));
    return data;
  }

  // Pulls data from the table to
  public ObservableList<MockData> getUpdatedDataFunc(TableView<MockData> table) {
    ObservableList<MockData> newData = FXCollections.observableArrayList();
    newData.addAll(table.getItems());
    return newData;
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
  public void acceptRow(MouseEvent e) {
    if (e.getSource()
        == btn_Accept) { // Checks if the request is getting accepted into the database
      // Get selected item and send it to the database with the updated information
      tbMockData
          .getItems()
          .removeAll(tbMockData.getSelectionModel().getSelectedItem()); // Removes the selected item
      // Needs to repopulate a NEW list with the updated components | Function returns a new
      newData.addAll(tbMockData.getItems());
    }
  }
}
