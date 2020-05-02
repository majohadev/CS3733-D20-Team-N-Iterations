package edu.wpi.N.entities.States;

import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.database.DBException;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;

  private StateSingleton() throws DBException {
    savedAlgo = new Algorithm();
  }

  public static StateSingleton getInstance() throws DBException {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }
}
