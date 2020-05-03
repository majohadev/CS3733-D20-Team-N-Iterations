package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class DFS extends AbsAlgo {

  /**
   * Finds a path from Start to Goal node using the DFS algorithm
   *
   * @param mapData: HashMap of the nodes and edges
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible
   * @return: Path object indicating a path to the Goal Node from Start Node
   * @throws DBException
   */
  @Override
  public Path findPath(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode startNode,
      DbNode endNode,
      boolean handicap) {
    try {

      // Initialize variables
      Stack<DbNode> stack = new Stack<>();
      Map<String, String> cameFrom = new HashMap<>();
      LinkedList<DbNode> visited = new LinkedList<>();

      // Add starting node to stack and cameFrom
      stack.push(startNode);
      cameFrom.put(startNode.getNodeID(), "");

      while (!stack.isEmpty()) {
        DbNode currNode = stack.pop();

        // if the goal node was found, break out of the loop
        if (currNode.equals(endNode)) {
          break;
        }

        visited.add(currNode);

        // Get the current nodes neighbors
        LinkedList<DbNode> neighbors = mapData.get(currNode.getNodeID());

        // Iterate through the neighbors
        for (DbNode nextNode : neighbors) {
          if (!visited.contains(nextNode) && !stack.contains(nextNode)) {
            // if handicap is selected and it's a stair, skip
            if (handicap && nextNode.getNodeType().equals("STAI")) {
              continue;
            } else {
              visited.add(nextNode);
              cameFrom.put(nextNode.getNodeID(), currNode.getNodeID());
              stack.push(nextNode);
            }
          }
        }
      }

      // Return path found
      return generatePath(startNode, endNode, cameFrom);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
