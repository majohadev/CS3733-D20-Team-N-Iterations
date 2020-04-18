package edu.wpi.N;

import edu.wpi.N.views.HomeController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
  private Stage primaryStage;
  private AnchorPane rootLayout;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("Brigham and Women's Hospital Kiosk Application");
    initRootLayout();
    showHome();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  public Stage getPrimaryStage() {
    return this.primaryStage;
  }

  public void initRootLayout() throws IOException {
    try {
      // Load root layout from fxml file
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("views/Home.fxml"));
      rootLayout = loader.load();

      // Show the scene containing the root layout
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);

      // Give the controller access to the main app
      HomeController controller = loader.getController();
      controller.setMainApp(this);

      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void showHome() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("views/Home.fxml"));
      AnchorPane pane = loader.load();

      Stage stage = new Stage();
      stage.setTitle("Home Screen");
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(primaryStage);

      Scene scene = new Scene(pane);
      stage.setScene(scene);

      HomeController controller = loader.getController();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
