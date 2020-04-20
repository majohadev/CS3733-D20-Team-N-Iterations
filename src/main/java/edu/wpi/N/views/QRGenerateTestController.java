package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import edu.wpi.N.App;
import edu.wpi.N.qrcontrol.QRGenerator;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class QRGenerateTestController extends QRGenerator implements Controller {

  @FXML JFXButton btn_generate;
  @FXML ImageView img_qrDisplay;
  @FXML TextField ta_fieldA, ta_fieldB, ta_fieldC;

  private App mainApp;

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  // When scan button is clicked
  @FXML
  public void onGenerateClicked() {
    ArrayList<String> lines = new ArrayList<String>();
    lines.add(ta_fieldA.getText());
    lines.add(ta_fieldB.getText());
    lines.add(ta_fieldC.getText());
    img_qrDisplay.setImage(generateImage(lines, true));
  }
}
