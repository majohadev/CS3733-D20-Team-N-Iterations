package edu.wpi.N.entities;

import edu.wpi.N.algorithms.Directions;
import edu.wpi.N.database.DBException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Path {
  private LinkedList<DbNode> path;
  private ArrayList<String> directions;

  // constructor
  public Path(LinkedList<DbNode> path) {
    this.path = path;
    this.directions =
        null; // only adds directions if you call getDirections so directions aren't generated
    // unnecessarily
  }

  public LinkedList<DbNode> getPath() {
    return this.path;
  }

  public ArrayList<String> getDirections() throws DBException {
    Directions dir = new Directions(this.path);
    this.directions = dir.getDirections();
    return this.directions;
  }
}
