package edu.wpi.N.entities.memento;

import edu.wpi.N.App;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.States.StateSingleton;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListener implements NativeMouseInputListener {

  private App mainApp = null;

  /**
   * provides reference to the main application class
   *
   * @param mainApp the main class of the application
   */
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void nativeMouseClicked(NativeMouseEvent e) {
    // System.out.println("Mouse Clicked: " + e.getClickCount());
    try {
      update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseClicked()");
    }
  }

  public void nativeMousePressed(NativeMouseEvent e) {
    // System.out.println("Mouse Pressed: " + e.getButton());
    try {
      update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMousePressed()");
    }
  }

  public void nativeMouseReleased(NativeMouseEvent e) {
    // System.out.println("Mouse Released: " + e.getButton());
    try {
      update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseReleased()");
    }
  }

  public void nativeMouseMoved(NativeMouseEvent e) {
    // System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    try {
      update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseMoved()");
    }
  }

  public void nativeMouseDragged(NativeMouseEvent e) {
    // System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    try {
      update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseDragged()");
    }
  }

  public void update() throws DBException {
    StateSingleton singleton = StateSingleton.getInstance();
    singleton.timer.purge();
    singleton.timer.cancel();
    singleton.timer = new Timer();
    TimerTask timerTask =
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  System.out.println("Timer Ended!");
                  System.out.println("Reset Kiosk!");
                  try {
                    singleton.originator.getStateFromMemento(singleton.careTaker.get(0));
                    String path = singleton.originator.getState();
                    switchTheScene(path);
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Why u no work? 2");
                  }
                });
          }
        };

    singleton.timer.schedule(timerTask, singleton.timeoutTime);
  }

  public void switchTheScene(String path) throws IOException, DBException {
    StateSingleton singleton = StateSingleton.getInstance();
    singleton.timer.cancel();
    singleton.timer.purge();
    mainApp.switchScene(path, singleton);
  }
}
