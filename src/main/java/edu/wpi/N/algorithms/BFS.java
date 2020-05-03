package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.*;

public class BFS extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the BFS algorithm
   *
   * @param mapData: HashMap of the nodes and edges
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible
   * @return: Path object indicating a short path to the Goal Node from Start Node
   */
  @Override
  public Path findPath(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode startNode,
      DbNode endNode,
      boolean handicap) {
    try {

      // Initialize variables
      Queue<DbNode> queue = new LinkedList<>();
      ArrayList<DbNode> checked = new ArrayList<>();
      Map<String, String> cameFrom = new HashMap<>();

      // Add start node to queue
      queue.add(startNode);
      cameFrom.put(startNode.getNodeID(), "");

      // While the queue isn't empty, keep looking for neighbors with the end node
      while (!queue.isEmpty()) {
        // Get the next node to check in the queue and mark it as checked
        DbNode currNode = queue.poll();
        checked.add(currNode);

        // Get the current nodes neighbors and check if it connects to the end node
        LinkedList<DbNode> neighbors = mapData.get(currNode.getNodeID());
        if (neighbors.contains(endNode)) {
          cameFrom.put(endNode.getNodeID(), currNode.getNodeID());
          break;
        }

        // If it doesn't connect with the end node, get all of its neighbors that aren't checked or
        // in the queue
        for (DbNode nextNode : neighbors) {
          if (handicap && nextNode.getNodeType().equals("STAI")) {
            continue;
          }
          if (!checked.contains(nextNode) && !queue.contains(nextNode)) {
            queue.add(nextNode);
            cameFrom.put(nextNode.getNodeID(), currNode.getNodeID());
          }
        }
      }

      // Generate and return the path in proper order
      return generatePath(startNode, endNode, cameFrom);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
