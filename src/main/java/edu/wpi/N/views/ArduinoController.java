package edu.wpi.N.views;

import com.fazecast.jSerialComm.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.States.StateSingleton;
import java.io.*;
import javafx.fxml.FXML;

public class ArduinoController implements Controller {

  private StateSingleton singleton;

  @Override
  public void setSingleton(StateSingleton singleton) {
    this.singleton = singleton;
  }

  private App mainApp;

  @FXML JFXButton btn_com;
  @FXML JFXButton btn_sendmsg;
  @FXML JFXButton btn_close;
  @FXML JFXButton btn_home;
  @FXML JFXButton btn_check;
  @FXML JFXButton btn_angle;
  @FXML JFXTextField txtf_com;
  @FXML JFXTextField txtf_msg;
  @FXML JFXTextField txtf_angle;
  @FXML JFXToggleButton tog_serial;

  private PrintWriter outPut;
  // private InputStream inPut;

  SerialPort arduinoPort = SerialPort.getCommPort("COM14");

  private int kioskAngle;

  public ArduinoController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() {
    arduinoPort.setBaudRate(9600);
    arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 200);
    try {
      MapDB.setKiosk("NHALL01904", 180);
    } catch (DBException e) {
      e.printStackTrace();
    }
    try {
      kioskAngle = MapDB.getKioskAngle();
    } catch (DBException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void toggleSerial() {
    boolean togSwitch = tog_serial.isSelected();
    if (togSwitch) {
      arduinoPort.openPort();
    } else arduinoPort.closePort();
  }

  @FXML
  public void setComPort() {
    String comPort = txtf_com.getText();
    arduinoPort = SerialPort.getCommPort(comPort);
  }

  @FXML
  private void sendMessage() {
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    String msg = txtf_msg.getText();
    outPut.print(msg);
    outPut.flush();
  }

  @FXML
  private void homeArrow() {
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    String msg = "home";
    outPut.print(msg);
    outPut.flush();
  }

  @FXML
  private void checkConnection() {
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    String msg = "check";
    outPut.print(msg);
    outPut.flush();
  }

  @FXML
  private void closeWindow() {
    // outPut = new PrintWriter(arduinoPort.getOutputStream());
    // outPut.print(msg);
    // outPut.flush();
  }

  @FXML
  private void displayKioskAngle() {
    String kioskAngs = String.valueOf(kioskAngle);
    txtf_angle.setText(kioskAngs);
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    outPut.print(kioskAngs);
    outPut.flush();
  }
}
