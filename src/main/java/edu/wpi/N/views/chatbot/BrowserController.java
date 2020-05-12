package edu.wpi.N.views.chatbot;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class BrowserController implements Initializable {
  @FXML private WebView webView;
  private String url;

  public BrowserController(String url) {
    this.url = url;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    WebEngine webEngine = webView.getEngine();
    webEngine.load(this.url);
  }
}
