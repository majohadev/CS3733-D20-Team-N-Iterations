package edu.wpi.N.entities.States;

import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.database.DBException;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapImageLoader mapImageLoader;
  public ChatMessagesState chatBotState;
  public boolean isMapDisplayActive;
  public boolean isServicesPageActive;

  private StateSingleton() throws DBException {
    savedAlgo = new Algorithm();
    mapImageLoader = new MapImageLoader();
    chatBotState = new ChatMessagesState();
  }

  public static StateSingleton getInstance() throws DBException {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }
}
