package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;

public class DFS extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the DFS algorithm
   *
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: Path object indicating the shortest path to the goal Node from Start Node
   * @throws DBException
   */
  @Override
  public Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException {
    return null;
  }
}
