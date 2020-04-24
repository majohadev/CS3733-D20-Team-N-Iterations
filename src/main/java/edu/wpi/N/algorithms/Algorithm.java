package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public class Algorithm {
  private IPathFinder pathFinder;

  public Algorithm() {
    this.pathFinder = new AStar();
  }

  public void setPathFinder(IPathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  public Path findPath(DbNode start, DbNode end) throws DBException {
    try {
      return pathFinder.findPath(start, end);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
