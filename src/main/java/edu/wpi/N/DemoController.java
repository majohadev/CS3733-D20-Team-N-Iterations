package edu.wpi.N;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class DemoController implements Initializable {
  @FXML public AnchorPane pane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    SanitationRequest request = new SanitationRequest();
    try {
      request.run(pane, null, 0);
    } catch (ServiceException e) {
      e.printStackTrace();
    }
  }
}
