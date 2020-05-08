package edu.wpi.N.entities;

import static java.lang.Math.atan2;

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

  public double getStartAngle(double kioskAngle) {
    double dy = path.get(1).getY() - path.get(0).getY();
    double dx = path.get(1).getX() - path.get(0).getX();
    double angle = Math.toDegrees(atan2(dy, dx));
    double angleChange = angle - kioskAngle;
    if (angleChange > 175) {
      angleChange -= 360;
    } else if (angleChange < -175) {
      angleChange += 360;
    }
    return angleChange + 90;
  }

  public boolean isEmpty() {
    return path.size() == 0;
  }

  public int size() {
    return path.size();
  }

  public DbNode get(int i) {
    return path.get(i);
  }

  public void clear() {
    path.clear();
  }
}
