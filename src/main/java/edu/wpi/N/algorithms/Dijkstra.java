package edu.wpi.N.algorithms;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.*;

public class Dijkstra extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using Dijkstra's algorithm
   *
   * @param mapData: HashMap of the nodes and edges
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible
   * @return: Path object indicating the shortest path to the Goal Node from Start Node
   */
  @Override
  public Path findPath(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode startNode,
      DbNode endNode,
      boolean handicap) {
    try {

      // Initialize variables
      PriorityQueue<DbNode> frontier = new PriorityQueue<DbNode>();
      frontier.add(startNode);
      Map<String, String> cameFrom = new HashMap<String, String>();
      Map<String, Double> costSoFar = new HashMap<String, Double>();
      cameFrom.put(startNode.getNodeID(), "");
      costSoFar.put(startNode.getNodeID(), 0.0);
      startNode.setScore(0);

      // While priority queue is not empty, get the node with highest Score (priority)
      while (!frontier.isEmpty()) {
        DbNode current = frontier.poll();

        // if the goal node was found, break out of the loop
        if (current.equals(endNode)) {
          break;
        }

        // Look at each neighboring node
        for (DbNode nextNode : mapData.get(current.getNodeID())) {
          // if handicap is selected and it's a stair, skip
          if (handicap && nextNode.getNodeType().equals("STAI")) {
            continue;
          } else {

            String nextNodeID = nextNode.getNodeID();

            // calculate the cost of next node
            double newCost = costSoFar.get(current.getNodeID()) + cost(nextNode, current);

            if (!costSoFar.containsKey(nextNodeID) || newCost < costSoFar.get(nextNodeID)) {
              // update the cost of nextNode
              costSoFar.put(nextNodeID, newCost);
              nextNode.setScore(newCost);

              // add to the priority queue
              frontier.add(nextNode);

              // keep track of where nodes come from
              // to generate the path to goal node
              cameFrom.put(nextNodeID, current.getNodeID());
            }
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
