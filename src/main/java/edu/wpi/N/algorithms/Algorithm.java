package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public class Algorithm {
  private PathFinder pathFinder;

  public Algorithm(PathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  public Path findPath(DbNode start, DbNode end) {
    return pathFinder.findPath(start, end);
  }
}
