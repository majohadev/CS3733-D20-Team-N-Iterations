package edu.wpi.N.views.mapDisplay;

import edu.wpi.N.algorithms.Direction;
import edu.wpi.N.qrcontrol.QRGenerator;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class QrPopUpController extends QRGenerator {
  @FXML private ImageView qrCode;

  /**
   * Displays qr-code on the map
   *
   * @param directions
   */
  public void displayQrCode(ArrayList<Direction> directions) {
    qrCode.setImage(generateImage(directions, false));
  }
}
