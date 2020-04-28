package edu.wpi.N.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class MapEditorEditNodeController {

  @FXML JFXTextField txt_shortName;
  @FXML JFXTextField txt_longName;
  @FXML JFXTextField txt_xPos;
  @FXML JFXTextField txt_yPos;
  @FXML JFXButton btn_cancel;
  @FXML JFXButton btn_confirm;

  public void setPos(double xPos, double yPos) {
    txt_xPos.setText(String.valueOf(xPos));
    txt_yPos.setText(String.valueOf(yPos));
  }

  public void setInfo(String shortName, String longName) {
    txt_shortName.setText(shortName);
    txt_longName.setText(longName);
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

  public void setShortName(String str) {
    txt_shortName.setText(str);
  }

  public String getLongName() {
    return txt_longName.getText();
  }

  public void setLongName(String str) {
    txt_longName.setText(str);
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

  public void clearAllFields() {
    txt_shortName.clear();
    txt_longName.clear();
    txt_xPos.clear();
    txt_yPos.clear();
  }
}
