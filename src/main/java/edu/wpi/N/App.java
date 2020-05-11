package edu.wpi.N;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
  private Stage masterStage;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException, DBException {
    // Configure the primary Stage

    this.masterStage = primaryStage;
    this.masterStage.setTitle("Brigham and Women's Hospital Kiosk Application");

    StateSingleton newSingleton = StateSingleton.getInstance();
    switchScene("views/mapDisplay/newMapDisplay.fxml", newSingleton);
    // switchScene("views/chatbot/chatBox.fxml", newSingleton);
    masterStage.setMaximized(true);
  }

  public Stage getStage() {
    return this.masterStage;
  }

  public void godPlzWork() {
    System.out.println("we gucci my nucci");
  }

  Action doSomething =
      new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          godPlzWork();
        }
      };

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  public void switchScene(String path, StateSingleton singleton) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(path));

    // Inject Singleton object into classes with Constructors that take StateSingleton
    loader.setControllerFactory(
        type -> {
          try {
            // look for constructor taking StateSingleton as a parameter
            for (Constructor<?> c : type.getConstructors()) {
              if (c.getParameterCount() == 1) {
                if (c.getParameterTypes()[0] == StateSingleton.class) {
                  return c.newInstance(singleton);
                }
              }
            }
            // didn't find appropriate constructor, just use default constructor:
            return type.getConstructor().newInstance();
          } catch (Exception exc) {
            throw new RuntimeException(exc);
          }
        });

    Pane pane = loader.load();
    Controller controller = loader.getController();
    controller.setMainApp(this);
    Scene scene = new Scene(pane);
    masterStage.setScene(scene);
    // masterStage.setMaximized(true);
    masterStage.setFullScreenExitHint("");
    masterStage.show();
  }
}
