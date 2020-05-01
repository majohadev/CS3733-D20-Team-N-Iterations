package edu.wpi.N.entities.States;

import edu.wpi.N.algorithms.Algorithm;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapImageLoader mapImageLoader;

  private StateSingleton() {
    savedAlgo = new Algorithm();
    mapImageLoader = new MapImageLoader();
  }

  public static StateSingleton getInstance() {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }
}
