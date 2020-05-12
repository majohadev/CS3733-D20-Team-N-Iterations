package edu.wpi.N.entities.memento;

import edu.wpi.N.entities.States.StateSingleton;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListener implements NativeMouseInputListener {

  public void nativeMouseClicked(NativeMouseEvent e) {
    //System.out.println("Mouse Clicked: " + e.getClickCount());
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseClicked()");
    }
  }

  public void nativeMousePressed(NativeMouseEvent e) {
    //System.out.println("Mouse Pressed: " + e.getButton());
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMousePressed()");
    }
  }

  public void nativeMouseReleased(NativeMouseEvent e) {
    //System.out.println("Mouse Released: " + e.getButton());
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseReleased()");
    }
  }

  public void nativeMouseMoved(NativeMouseEvent e) {
    //System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseMoved()");
    }
  }

  public void nativeMouseDragged(NativeMouseEvent e) {
    //System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    try {
      StateSingleton singleton = StateSingleton.getInstance();
      singleton.update();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Failed at GlobalMouseListener - nativeMouseDragged()");
    }
  }
}
