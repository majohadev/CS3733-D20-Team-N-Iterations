package edu.wpi.N.views;

import static java.lang.Math.abs;

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

  private static PrintWriter outPut;
  private static SerialPort arduinoPort = SerialPort.getCommPort("COM14");
  private static double arrowAngle = 0;
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

  private int kioskAngle;

  public ArduinoController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() {
    arduinoPort.setBaudRate(9600);
    arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 200);
    /*try {
      if (MapDB.getKiosk() == null) MapDB.setKiosk("NSERV00301", 180);
    } catch (DBException e) {
      e.printStackTrace();
    }
    try {
      kioskAngle = MapDB.getKioskAngle();
    } catch (DBException e) {
      e.printStackTrace();
    }*/
    System.out.println("leggo");
    arduinoPort.openPort();
    arrowAngle = 0;
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
  public void homeArrow() {
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    String msg = "home" + kioskAngle;
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

  @FXML
  public static void turnArrow(double angle) throws DBException {
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    if (arrowAngle == MapDB.getKioskAngle()) {
      if (angle >= 0) {
        outPut.print("f" + angle);
        arrowAngle += angle;
      } else {
        arrowAngle += angle;
        outPut.print("b" + abs(angle));
        arrowAngle -= 360 * (arrowAngle / 360);
        if (arrowAngle < 0) arrowAngle += 360;
      }
    } else {
      int turnAngle = (int) (MapDB.getKioskAngle() + angle);
      turnAngle -= 360 * (turnAngle / 360);
      if (turnAngle < 0) turnAngle += 360;
      setUpArrow(turnAngle);
    }
    outPut.flush();
  }

  @FXML
  public static void setUpArrow(double angle) {
    String kioskAngs = String.valueOf((int) angle);
    arrowAngle = angle;
    String output = "h" + kioskAngs;
    System.out.println(output);
    outPut = new PrintWriter(arduinoPort.getOutputStream());
    outPut.print(output);
    outPut.flush();
    arrowAngle = angle;
  }

  @FXML
  public static void resetToCurrentKiosk() {
    try {
      arrowAngle = MapDB.getKioskAngle();
    } catch (DBException e) {
      e.printStackTrace();
    }
    setUpArrow(arrowAngle);
  }
}
