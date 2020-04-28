package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class MapEditorAddNodeController {

  final double IMAGE_WIDTH = 2475;
  final double IMAGE_HEIGHT = 1485;
  final double MAP_WIDTH = 1661;
  final double MAP_HEIGHT = 997;
  final double HORIZONTAL_SCALE = MAP_WIDTH / IMAGE_WIDTH;
  final double VERTICAL_SCALE = MAP_HEIGHT / IMAGE_HEIGHT;

  @FXML JFXTextField txt_shortName;
  @FXML JFXTextField txt_longName;
  @FXML JFXTextField txt_xPos;
  @FXML JFXTextField txt_yPos;
  @FXML JFXComboBox cb_type;
  @FXML JFXButton btn_confirm;
  @FXML JFXButton btn_cancel;

  ObservableList<String> types =
      FXCollections.observableArrayList(
          "HALL", "ELEV", "REST", "STAI", "DEPT", "LABS", "INFO", "CONF", "EXIT", "RETL", "SERV");

  public void initialize() {
    cb_type.setItems(types);
  }

  public void setPos(double xPos, double yPos) {
    txt_xPos.setText(String.valueOf(xPos));
    txt_yPos.setText(String.valueOf(yPos));
  }

  public String getXPos() {
    return txt_xPos.getText();
  }

  public String getYPos() {
    return txt_yPos.getText();
  }

  public String getShortName() {
    return txt_shortName.getText();
  }

  public String getLongName() {
    return txt_longName.getText();
  }

  public String getType() {
    return (String) cb_type.getValue();
  }

  public JFXTextField getTxtXPos() {
    return txt_xPos;
  }

  public JFXTextField getTxtYPos() {
    return txt_yPos;
  }

  public JFXButton getBtnCancel() {
    return btn_cancel;
  }

  public JFXButton getBtnConfirm() {
    return btn_confirm;
  }
}
