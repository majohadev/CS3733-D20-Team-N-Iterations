package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.*;
import org.bridj.util.Pair;

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
  public static Path findPath(DbNode startNode, DbNode endNode) {
    try {
      //            // Check if start is on the same floor as end
      //            if (startNode.getFloor() != endNode.getFloor()) {
      //                // If not, find path to the closes elevator (prioritized) or stairs which is
      // connected to
      //                // endNode floor
      //                DbNode bestFloorChange = getFloorChange(startNode, endNode);
      //
      //                // Get path from start to elevator / stairs
      //                Path pathSoFar = findPath(startNode, bestFloorChange);
      //
      //                // Get corresponding elevator/stair node on goal node's floor
      //                DbNode floorChangeNode =
      //                        getFloorChangeNode(bestFloorChange, bestFloorChange.getFloor(),
      //                                endNode.getFloor());
      //
      //                // Add path from elevator on goal node's floor to the goal node
      //                pathSoFar.getPath().addAll(findPath(floorChangeNode, endNode).getPath());
      //
      //                return pathSoFar;
      //            }

      // get floors of start and end nodes
      int startFloorNum = startNode.getFloor();
      int endFloorNum = endNode.getFloor();

      // If yes, find path from elevator to the end Node
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
            MapDB.getGAdjacent(current.ID, startFloorNum, endFloorNum);

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
      path.add(MapDB.getNode(currentID));

      try {
        while (!currentID.equals(start.ID)) {
          currentID = cameFrom.get(currentID);
          path.addFirst(MapDB.getNode(currentID));
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
          MapDB.searchNode(start.getFloor(), start.getBuilding(), nodeType, "");
      if (!nodes.isEmpty()) {
        double closest =
            cost(MapDB.getGNode(start.getNodeID()), MapDB.getGNode(nodes.getFirst().getNodeID()));
        DbNode end = nodes.getFirst();
        for (DbNode n : nodes) {
          double cost = cost(MapDB.getGNode(start.getNodeID()), MapDB.getGNode(n.getNodeID()));
          if (cost <= closest) {
            closest = cost;
            end = n;
          }
        }
        return findPath(start, end);
      } else {
        return null;
      }
    } catch (DBException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Determines if elevators or stairs lead to a better path
   *
   * @param startNode: Starting node
   * @param endNode: End Node
   * @throws DBException
   * @return: Elevator or Stair node
   */
  private static DbNode getFloorChange(DbNode startNode, DbNode endNode) throws DBException {
    Pair<DbNode, Double> bestElevator = getBestElevator(startNode, endNode);
    Pair<DbNode, Double> bestStair = getBestStair(startNode, endNode);

    if (bestElevator.getKey() == null && bestStair.getKey() == null) {
      return null;

    } else if (bestElevator.getKey() == null) {
      return bestStair.getKey();

    } else if (bestStair.getKey() == null) {
      return bestElevator.getKey();

    } else if (bestElevator.getValue() <= bestStair.getValue() * 1.5) {
      return bestElevator.getKey();

    } else return bestStair.getKey();
  }

  /**
   * Finds the best elevator to take based on score
   *
   * @param startNode: Starting Node
   * @param endNode: End Node
   * @throws DBException
   * @return: Elevator node and its score
   */
  private static Pair<DbNode, Double> getBestElevator(DbNode startNode, DbNode endNode)
      throws DBException {
    LinkedList<DbNode> elevators =
        MapDB.searchNode(startNode.getFloor(), startNode.getBuilding(), "ELEV", "");
    DbNode lowestSoFar = null;
    double score = 1000000;
    for (DbNode currentElevator : elevators) {
      if (isEligibleFloorChange(currentElevator, currentElevator.getFloor(), endNode.getFloor())) {
        Node elevator = MapDB.getGNode(currentElevator.getNodeID());
        double currentCost = cost(MapDB.getGNode(startNode.getNodeID()), elevator);
        double currentScore =
            currentCost + heuristic(elevator, MapDB.getGNode(endNode.getNodeID()));

        if (currentScore < score) {
          lowestSoFar = currentElevator;
          score = currentScore;
        }
      }
    }
    return new Pair<DbNode, Double>(lowestSoFar, score);
  }

  /**
   * Finds the best stairs to take based on score
   *
   * @param startNode: Starting Node
   * @param endNode: End Node
   * @throws DBException
   * @return: Stair node and its score
   */
  private static Pair<DbNode, Double> getBestStair(DbNode startNode, DbNode endNode)
      throws DBException {
    LinkedList<DbNode> stairs =
        MapDB.searchNode(startNode.getFloor(), startNode.getBuilding(), "STAI", "");
    DbNode lowestSoFar = null;
    double score = 1000000;
    for (DbNode currentStair : stairs) {
      if (isEligibleFloorChange(currentStair, currentStair.getFloor(), endNode.getFloor())) {
        Node stair = MapDB.getGNode(currentStair.getNodeID());
        double currentCost = cost(MapDB.getGNode(startNode.getNodeID()), stair);
        double currentScore = currentCost + heuristic(stair, MapDB.getGNode(endNode.getNodeID()));

        if (currentScore < score) {
          lowestSoFar = currentStair;
          score = currentScore;
        }
      }
    }
    return new Pair<DbNode, Double>(lowestSoFar, score);
  }

  /**
   * Determines if a elevator or stair node can reach the goal floor
   *
   * @param floorChange: Elevator or Stair Node
   * @param currentFloor: Current floor
   * @param floorGoal: Goal floor
   * @throws DBException
   * @return: True if elevator or stair can reach the goal floor, false otherwise
   */
  public static boolean isEligibleFloorChange(DbNode floorChange, int currentFloor, int floorGoal)
      throws DBException {
    LinkedList<DbNode> adjacentNodes = MapDB.getAdjacent(floorChange.getNodeID());
    String floorType = floorChange.getNodeType();

    // Going up
    if (floorGoal - currentFloor > 0) {
      for (DbNode currentNode : adjacentNodes) {
        int currentNodeFloor = currentNode.getFloor();
        if (currentNode.getNodeType().equals(floorType)
            && currentNodeFloor > currentFloor
            && floorGoal >= currentNodeFloor) {
          return isEligibleFloorChange(currentNode, currentNodeFloor, floorGoal);
        }
      }
      return false;
    }
    // Going down
    else if (floorGoal - currentFloor < 0) {
      for (DbNode currentNode : adjacentNodes) {
        int currentNodeFloor = currentNode.getFloor();
        if (currentNode.getNodeType().equals(floorType)
            && currentNodeFloor < currentFloor
            && floorGoal <= currentNodeFloor) {
          return isEligibleFloorChange(currentNode, currentNodeFloor, floorGoal);
        }
      }
      return false;
    }
    // On same floor
    else {
      return true;
    }
  }

  /**
   * Returns respective elevator or stair node on the Goal Node's floor corresponding to given
   * elevator or stair if exist
   *
   * @param floorChange: Elevator or Stair Node
   * @param currentFloor: Current floor
   * @param floorGoal: Goal floor
   * @throws DBException
   * @return: The elevator or stair node on the goal floor that is accessible by the given elevator
   *     or stair node, null if none
   */
  public static DbNode getFloorChangeNode(DbNode floorChange, int currentFloor, int floorGoal)
      throws DBException {
    LinkedList<DbNode> adjacentNodes = MapDB.getAdjacent(floorChange.getNodeID());
    String floorType = floorChange.getNodeType();

    // Going up
    if (floorGoal - currentFloor > 0) {
      for (DbNode currentNode : adjacentNodes) {
        int currentNodeFloor = currentNode.getFloor();
        if (currentNode.getNodeType().equals(floorType)
            && currentNodeFloor > currentFloor
            && floorGoal >= currentNodeFloor) {
          return getFloorChangeNode(currentNode, currentNodeFloor, floorGoal);
        }
      }
      return null;
    }
    // Going down
    else if (floorGoal - currentFloor < 0) {
      for (DbNode currentNode : adjacentNodes) {
        int currentNodeFloor = currentNode.getFloor();
        if (currentNode.getNodeType().equals(floorType)
            && currentNodeFloor < currentFloor
            && floorGoal <= currentNodeFloor) {
          return getFloorChangeNode(currentNode, currentNodeFloor, floorGoal);
        }
      }
      return null;
    }
    // On same floor
    else {
      return floorChange;
    }
  }
}
