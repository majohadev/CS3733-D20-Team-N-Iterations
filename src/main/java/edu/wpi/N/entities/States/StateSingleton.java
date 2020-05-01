package edu.wpi.N.entities.States;

import edu.wpi.N.algorithms.Algorithm;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapDataStorage mapData;

  private StateSingleton() {
    savedAlgo = new Algorithm();
    mapData = new MapDataStorage();
  }

  public static StateSingleton getInstance() {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }
}
