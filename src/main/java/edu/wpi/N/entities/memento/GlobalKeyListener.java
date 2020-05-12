package edu.wpi.N.entities.memento;

import edu.wpi.N.App;
import edu.wpi.N.entities.States.StateSingleton;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

  private App mainApp = null;

  /**
   * provides reference to the main application class
   *
   * @param mainApp the main class of the application
   */
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void nativeKeyPressed(NativeKeyEvent e) {
    // System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

    if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
      try {
        GlobalScreen.unregisterNativeHook();
      } catch (NativeHookException ex) {
        ex.printStackTrace();
      }
    }

    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalKeyListener - nativeKeyPressed()");
    }
  }

  public void nativeKeyReleased(NativeKeyEvent e) {
    // System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalKeyListener - nativeKeyReleased()");
    }
  }

  public void nativeKeyTyped(NativeKeyEvent e) {
    // System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalKeyListener - nativeKeyTyped()");
    }
  }

  //  public void switchTheScene(String path) throws IOException, DBException {
  //    StateSingleton singleton = StateSingleton.getInstance();
  //    singleton.timer.cancel();
  //    singleton.timer.purge();
  //    mainApp.switchScene(path, singleton);
  //  }
}
