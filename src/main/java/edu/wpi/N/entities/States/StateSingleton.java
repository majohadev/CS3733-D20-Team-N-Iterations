package edu.wpi.N.entities.States;

import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.memento.CareTaker;
import edu.wpi.N.entities.memento.Originator;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapImageLoader mapImageLoader;
  public ChatMessagesState chatBotState;
  public String algoState;
  public Originator originator;
  public CareTaker careTaker;
  public int timeoutTime;
  public Timer timer;
  private App mainApp = null;

  private StateSingleton() throws DBException {
    savedAlgo = new Algorithm();
    mapImageLoader = new MapImageLoader();
    chatBotState = new ChatMessagesState();
    algoState = "AStar";
    originator = new Originator();
    careTaker = new CareTaker();

    // Set up memento pattern
    originator.setState("views/mapDisplay/newMapDisplay.fxml");
    careTaker.add(originator.saveStateToMemento());

    // Initialize timeout time to 15000 milliseconds (15 seconds)
    timeoutTime = 15000;
  }

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void setTimeoutTime(int newTimeoutTime) {
    timeoutTime = newTimeoutTime;
    update();
  }

  /**
   * Function that switches the scene back to map display on timeout
   *
   * @param path: String that is the path a the .fxml file
   * @throws IOException
   */
  public void switchTheScene(String path) throws IOException {
    timer.cancel();
    timer.purge();
    try {
      chatBotState.closeSession();
    } catch (NullPointerException ex) {
      System.out.println("Chatbot isn't open, don't need to close it");
    }
    mainApp.switchScene(path, this);
  }

  /**
   * Either creates the initial instance of the singleton or returns the singleton
   *
   * @return: Singleton
   * @throws DBException
   */
  public static StateSingleton getInstance() throws DBException {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }

  /** Update/Reset the timer in the singleton */
  public void update() {
    if (this.timer != null) {
      timer.purge();
      timer.cancel();
    }
    timer = new Timer();
    TimerTask timerTask =
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  System.out.println("Timer Ended!");
                  System.out.println("Reset Kiosk!");
                  try {
                    originator.getStateFromMemento(careTaker.get(0));
                    String path = originator.getState();
                    switchTheScene(path);
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Why u no work? - update()");
                  }
                });
          }
        };

    timer.schedule(timerTask, timeoutTime);
  }
}
