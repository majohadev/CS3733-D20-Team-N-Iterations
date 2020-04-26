package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.*;

public class AStar extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the A* algorithm
   *
   * @param startNode: The start node
   * @param endNode: The destination node
   * @param handicap: Boolean saying whether path should be handicap accessible
   * @return: Path object indicating the shortest path to the Goal Node from Start Node
   * @throws DBException
   */
  @Override
  public Path findPath(DbNode startNode, DbNode endNode, boolean handicap) throws DBException {
    try {
      // TODO: delete later
      System.out.println("Astar!!!!");
      int startFloorNum = startNode.getFloor();
      int endFloorNum = endNode.getFloor();

      Node start = MapDB.getGNode(startNode.getNodeID());
      Node end = MapDB.getGNode(endNode.getNodeID());

      // Initialize variables
      PriorityQueue<Node> frontier = new PriorityQueue<Node>();
      frontier.add(start);
      Map<String, String> cameFrom = new HashMap<String, String>();
      Map<String, Double> costSoFar = new HashMap<String, Double>();
      cameFrom.put(start.ID, "");
      costSoFar.put(start.ID, 0.0);
      start.score = 0;

      // While priority queue is not empty, get the node with highest Score (priority)
      while (!frontier.isEmpty()) {
        Node current = frontier.poll();

        // if the goal node was found, break out of the loop
        if (current.equals(end)) {
          break;
        }

        // for every node (next node), current node has edge to:
        LinkedList<Node> adjacentToCurrent =
            MapDB.getGAdjacent(current.ID, startFloorNum, endFloorNum, handicap);

        for (Node nextNode : adjacentToCurrent) {
          String nextNodeID = nextNode.ID;

          // calculate the cost of next node
          double newCost = costSoFar.get(current.ID) + cost(nextNode, current);

          if (!costSoFar.containsKey(nextNodeID) || newCost < costSoFar.get(nextNodeID)) {
            // update the cost of nextNode
            costSoFar.put(nextNodeID, newCost);
            // calculate and update the Score of nextNode
            double priority = newCost + heuristic(nextNode, end);

            nextNode.score = priority;
            // add to the priority queue
            frontier.add(nextNode);
            // keep track of where nodes come from
            // to generate the path to goal node
            cameFrom.put(nextNodeID, current.ID);
          }
        }
      }

      // Generate and return the path in proper order
      return generatePath(start, end, cameFrom);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
