package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.*;

public class BFS extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the BFS algorithm (Dijkstra's algorithm)
   *
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: Path object indicating the shortest path to the goal Node from Start Node
   * @throws DBException
   */
  @Override
  public Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException {
    try {

      Queue<DbNode> queue = new LinkedList<>();
      ArrayList<Node> checked = new ArrayList<>();
      LinkedList<DbNode> path = new LinkedList<>();

      queue.add(startNode);
      // Make new BFS Node that contains itself and where it came from?

      while (!queue.isEmpty()) {
        DbNode currNode = queue.poll();

        LinkedList<DbNode> neighbors = MapDB.getAdjacent(currNode.getNodeID());
        if (neighbors.contains(endNode)) {
          path.add(endNode);
        }

        for (DbNode nextNode : neighbors) {
          if (!checked.contains(nextNode) && !queue.contains(nextNode)) {
            queue.add(nextNode);
          }
        }
      }

      //      if(true) {
      //        String nextNodeID = nextNode.getNodeID();
      //        if (nextNodeID.equals(endNode.getNodeID())) {
      //          path.add(MapDB.getNode(nextNodeID));
      //        }
      //      }

      return null;
    } catch (Exception e) {
      e.printStackTrace();
      throw new DBException("Unknown error: findPath in BFS", e);
    }
  }
}
