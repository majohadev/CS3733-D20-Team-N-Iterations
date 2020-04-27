package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public class Algorithm extends AbsAlgo {
  private IPathFinder pathFinder;

  public Algorithm() {
    this.pathFinder = new AStar();
  }

  // Getter
  public IPathFinder getPathFinder(IPathFinder pathFinder) {
    return this.pathFinder;
  }

  // Setter
  public void setPathFinder(IPathFinder pathFinder) {
    this.pathFinder = pathFinder;
  }

  /**
   * Finds the shortest path from Start to Goal node using the algorithm that pathfinder is set to
   *
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: Path object indicating the shortest path to the goal Node from Start Node
   * @throws DBException
   */
  public Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException {
    try {
      return pathFinder.findPath(startNode, endNode, handicap);
    } catch (DBException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
