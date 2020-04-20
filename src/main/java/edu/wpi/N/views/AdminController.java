package edu.wpi.N.views;

import edu.wpi.N.App;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController implements Initializable {

  private App mainApp;

  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;

  @FXML TableView<MockData> tbMockData = new TableView<MockData>();

  // @FXML TableColumn<MockData, String> tcData;
  TableColumn tcDataCol = new TableColumn();

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    tbMockData.setItems(mockData);

    tcDataCol.setCellValueFactory(new PropertyValueFactory<MockData, String>("First Name"));
  }

  private ObservableList<MockData> mockData =
      FXCollections.observableArrayList(new MockData("Hello"), new MockData("Goodbye"));
}
