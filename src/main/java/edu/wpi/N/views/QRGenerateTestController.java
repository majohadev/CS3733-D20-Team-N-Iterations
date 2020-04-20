package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.qrcontrol.QRGenerator;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class QRGenerateTestController extends QRGenerator {

  @FXML JFXButton btn_generate;
  @FXML ImageView qrDisplay;

  // When scan button is clicked
  @FXML
  public void onGenerateClicked() {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("Cool");
    lines.add("and");
    lines.add("good.");
    qrDisplay.setImage(generateImage(lines, false));
  }
}
