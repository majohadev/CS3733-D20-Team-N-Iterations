package edu.wpi.N.views;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class drawercontroller {

  @FXML JFXTextField txtf_test;

  public void printLine() {
    System.out.println(txtf_test.getText());
  }
}
