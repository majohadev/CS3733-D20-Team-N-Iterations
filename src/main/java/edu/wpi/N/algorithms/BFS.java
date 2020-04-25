package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.*;

public class BFS extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the BFS algorithm
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
      ArrayList<DbNode> checked = new ArrayList<>();
      Map<String, String> cameFrom = new HashMap<>();

      queue.add(startNode);
      cameFrom.put(startNode.getNodeID(), "");

      while (!queue.isEmpty()) {
        DbNode currNode = queue.poll();
        checked.add(currNode);

        LinkedList<DbNode> neighbors = MapDB.getAdjacent(currNode.getNodeID());
        if (neighbors.contains(endNode)) {
          cameFrom.put(endNode.getNodeID(), currNode.getNodeID());
          break;
        }

        for (DbNode nextNode : neighbors) {
          if (!checked.contains(nextNode) && !queue.contains(nextNode)) {
            queue.add(nextNode);
            cameFrom.put(nextNode.getNodeID(), currNode.getNodeID());
          }
        }
      }

      // Generate and return the path in proper order
      return generatePath(
          MapDB.getGNode(startNode.getNodeID()), MapDB.getGNode(endNode.getNodeID()), cameFrom);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
