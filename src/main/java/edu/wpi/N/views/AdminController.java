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

  @FXML Button btn_logout;
  @FXML Button btn_laundryReq;
  @FXML Button btn_transReq;

  @FXML TableView<MockData> tbMockData = new TableView<MockData>();
  // @FXML TableColumn<MockData, String> data;

  @Override
  public void initialize(URL location, ResourceBundle resourceBundle) {
    TableColumn<MockData, String> data = new TableColumn<>("Data");
    data.setMinWidth(100);
    data.setCellValueFactory(new PropertyValueFactory<MockData, String>("data"));
    tbMockData.setItems(getMockDataFunc());
    tbMockData.getColumns().addAll(data);
  }

  public ObservableList<MockData> getMockDataFunc() {
    ObservableList<MockData> data = FXCollections.observableArrayList();
    data.add(new MockData("Hello"));
    data.add(new MockData("Goodbye"));
    return data;
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
}
