package edu.wpi.N.views.admin;

import com.jfoenix.controls.*;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.*;
import edu.wpi.N.database.*;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {

      setTitleLabel();

    } catch (DBException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText(e.getMessage());
      errorAlert.show();
    }
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
              return type.getConstructor().newInstance(singleton);
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
}
