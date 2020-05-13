package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.MainClass;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewController extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage primaryStage) {
    primaryStage.setTitle("JavaFX WebView Example");

    WebView webView = new WebView();

    webView
        .getEngine()
        .load(MainClass.class.getResource("views/FaulknerToMain45Francis.html").toString());

    VBox vBox = new VBox(webView);
    Scene scene = new Scene(vBox, 960, 600);

    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
