package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class DFS extends AbsAlgo {

  /**
   * Finds a path from Start to Goal node using the DFS algorithm
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
      System.out.println("DFS!!!!");
      // Initialize variables
      Stack<Node> stack = new Stack<>();
      Map<String, String> cameFrom = new HashMap<>();
      LinkedList<Node> visited = new LinkedList<>();
      Node start = MapDB.getGNode(startNode.getNodeID());
      Node end = MapDB.getGNode(endNode.getNodeID());
      int startFloorNum = startNode.getFloor();
      int endFloorNum = endNode.getFloor();

      // Add starting node to stack and cameFrom
      stack.push(start);
      cameFrom.put(start.ID, "");

      while (!stack.isEmpty()) {
        Node currNode = stack.pop();
        visited.add(currNode);

        // Get the current nodes neighbors and check if it connects to the end node
        LinkedList<Node> neighbors =
            MapDB.getGAdjacent(currNode.ID, startFloorNum, endFloorNum, handicap);
        if (neighbors.contains(endNode)) {
          cameFrom.put(endNode.getNodeID(), currNode.ID);
          break;
        }

        for (Node nextNode : neighbors) {
          if (!visited.contains(nextNode) && !stack.contains(nextNode)) {
            visited.add(nextNode);
            cameFrom.put(nextNode.ID, currNode.ID);
            stack.push(nextNode);
          }
        }
      }

      // Return path found
      return generatePath(start, end, cameFrom);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  //  /**
  //   * Finds a path from Start to Goal node using DFS and recursion
  //   *
  //   * @param start: The start node
  //   * @param end: The end node
  //   * @param cameFrom: A hashmap that keeps track of which node visited which
  //   * @param visited: A list of nodes that have been visited so far
  //   * @param handicap: Boolean saying whether path should be handicap accessible
  //   * @return: Path object indicating a path to the Goal node from the Start node
  //   * @throws DBException
  //   */
  //  private Path DFSRecursion(Node start, Node end, Map<String, String> cameFrom, LinkedList<Node>
  // visited, boolean handicap) throws DBException {
  //    //Get the floor numbers of the
  //    // Get the current nodes neighbors and check if it connects to the end node
  //    LinkedList<Node> neighbors =
  //            MapDB.getGAdjacent(start.ID, startFloorNum, endFloorNum, handicap);
  //    if (neighbors.contains(endNode)) {
  //      cameFrom.put(endNode.getNodeID(), currNode.ID);
  //
  //    }
  //
  //
  //    //Return path found
  //    return generatePath(start, end, cameFrom);
  //  }
}
