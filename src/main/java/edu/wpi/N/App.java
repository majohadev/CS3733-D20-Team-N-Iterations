package edu.wpi.N;

import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import edu.wpi.N.views.HomeController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
  private Stage masterStage;
  public static HomeController homeController = new HomeController();

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // Configure the primary Stage
    this.masterStage = primaryStage;
    this.masterStage.setTitle("Brigham and Women's Hospital Kiosk Application");
    StateSingleton newSingleton = StateSingleton.getInstance();
    switchScene("views/home.fxml", newSingleton);
    masterStage.setMaximized(true);
  }

  public Stage getStage() {
    return this.masterStage;
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  public void switchScene(String path, StateSingleton singleton) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(path));
    Pane pane = loader.load();

    Scene scene = new Scene(pane);
    masterStage.setScene(scene);
    // masterStage.setMaximized(true);
    masterStage.setFullScreenExitHint("");
    masterStage.show();
    Controller controller = loader.getController();
    controller.setMainApp(this);
    controller.setSingleton(singleton);
  }
}
