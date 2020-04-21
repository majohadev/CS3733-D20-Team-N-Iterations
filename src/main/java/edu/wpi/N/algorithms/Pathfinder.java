package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.*;

public class Pathfinder {

  /**
   * Function calculates Euclidean distance between the next Node and current Node (cost of given
   * node)
   *
   * @param currNode: current Node
   * @param nextNode: next Node
   * @return Euclidean distance from the start
   */
  public static double cost(Node currNode, Node nextNode) {
    return Math.sqrt(
        Math.pow(nextNode.getX() - currNode.getX(), 2)
            + Math.pow(nextNode.getY() - currNode.getY(), 2));
  }

  /**
   * Function calculates Manhatten distance between goal and current Node
   *
   * @param currNode: current Node
   * @return Manhattan distance to the goal Node
   */
  public static double heuristic(Node currNode, Node end) {
    return Math.abs(end.getX() - currNode.getX()) + Math.abs(end.getY() - currNode.getY());
  }

  /**
   * Finds the shortest path from Start to Goal node
   *
   * @return Path object indicating the shortest path to the goal Node from Start Node
   */
  public static Path findPath(String startID, String endID) {
    try {
      Node start = DbController.getGNode(startID);
      Node end = DbController.getGNode(endID);

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
        if (current == end) {
          break;
        }

        // for every node (next node), current node has edge to:
        LinkedList<Node> adjacentToCurrent = DbController.getGAdjacent(current.ID);
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

  /**
   * Helper function which generates Path given a Map
   *
   * @param cameFrom: Map, where key: NodeID, value: came-from-NodeID
   * @return Path object containing generated path
   */
  private static Path generatePath(Node start, Node end, Map<String, String> cameFrom) {
    try {
      String currentID = end.ID;
      LinkedList<DbNode> path = new LinkedList<DbNode>();
      path.add(DbController.getNode(currentID));

      try {
        while (!currentID.equals(start.ID)) {
          currentID = cameFrom.get(currentID);
          path.addFirst(DbController.getNode(currentID));
        }
      } catch (NullPointerException e) {
        System.out.println("Location was not found.");
        throw e;
      }

      Path finalPath = new Path(path);

      return finalPath;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @param start, DbNode of starting node
   * @param nodeType, String the type of node you want (must be length 4)
   * @return Path, path from start node to closest (eucledian) end node of requested type
   * @throws DBException
   */
  public static Path findQuickAccess(DbNode start, String nodeType) throws DBException {
    try {
      LinkedList<DbNode> nodes =
          DbController.searchNode(start.getFloor(), start.getBuilding(), nodeType, "");
      if (!nodes.isEmpty()) {
        double closest =
            cost(
                DbController.getGNode(start.getNodeID()),
                DbController.getGNode(nodes.getFirst().getNodeID()));
        DbNode end = nodes.getFirst();
        for (DbNode n : nodes) {
          double cost =
              cost(DbController.getGNode(start.getNodeID()), DbController.getGNode(n.getNodeID()));
          if (cost <= closest) {
            closest = cost;
            end = n;
          }
        }
        return findPath(start.getNodeID(), end.getNodeID());
      } else {
        return null;
      }
    } catch (DBException e) {
      e.printStackTrace();
      return null;
    }
  }
}
