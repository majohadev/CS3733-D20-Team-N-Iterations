package edu.wpi.N.entities.States;

import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.memento.CareTaker;
import edu.wpi.N.entities.memento.Originator;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapImageLoader mapImageLoader;
  public ChatMessagesState chatBotState;
  public Originator originator;
  public CareTaker careTaker;
  public int timeoutTime;

  private StateSingleton() throws DBException {
    savedAlgo = new Algorithm();
    mapImageLoader = new MapImageLoader();
    chatBotState = new ChatMessagesState();
    originator = new Originator();
    careTaker = new CareTaker();
    // Initialize timeout time to 15000 milliseconds (15 seconds)
    timeoutTime = 15000;
  }

  public static StateSingleton getInstance() throws DBException {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }
}
