package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class AlgoTemplate extends AbsAlgo {

  /**
   * Abstract method that is overridden by AStar and Dijkstra pathfinders (template)
   *
   * @param mapData: HashMap of the nodes and edges
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible
   * @return: A path of nodes from the start to the end node
   */
  public abstract Path findPath(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode startNode,
      DbNode endNode,
      boolean handicap);
}
