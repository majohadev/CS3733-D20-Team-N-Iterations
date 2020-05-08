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

        // if the goal node was found, break out of the loop
        if (currNode.equals(endNode)) {
          break;
        }

        // Get the current nodes neighbors and check if it connects to the end node
        LinkedList<DbNode> neighbors = mapData.get(currNode.getNodeID());

        // Look at each neighboring node
        for (DbNode nextNode : neighbors) {
          // if handicap is selected and it's a stair, skip
          if (handicap && nextNode.getNodeType().equals("STAI")) {
            continue;
          }
          // If the neighbor hasn't been checked or isn't in the queue
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
