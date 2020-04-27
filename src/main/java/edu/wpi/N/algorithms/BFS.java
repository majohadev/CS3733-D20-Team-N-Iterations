package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.*;

public class BFS extends AbsAlgo {

  /**
   * Finds the shortest path from Start to Goal node using the BFS algorithm
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
      System.out.println("BFS!!!!");
      // Initialize variables
      Queue<Node> queue = new LinkedList<>();
      ArrayList<Node> checked = new ArrayList<>();
      Map<String, String> cameFrom = new HashMap<>();

      // Get start and end floor
      int startFloorNum = startNode.getFloor();
      int endFloorNum = endNode.getFloor();

      // Get Node version of  start and end DbNodes
      Node start = MapDB.getGNode(startNode.getNodeID());
      Node end = MapDB.getGNode(endNode.getNodeID());

      // Add start node to queue
      queue.add(start);
      cameFrom.put(start.ID, "");

      // While the queue isn't empty, keep looking for neighbors with the end node
      while (!queue.isEmpty()) {
        // Get the next node to check in the queue and mark it as checked
        Node currNode = queue.poll();
        checked.add(currNode);

        // Get the current nodes neighbors and check if it connects to the end node
        LinkedList<Node> neighbors =
            MapDB.getGAdjacent(currNode.ID, startFloorNum, endFloorNum, handicap);
        if (neighbors.contains(endNode)) {
          cameFrom.put(endNode.getNodeID(), currNode.ID);
          break;
        }

        // If it doesn't connect with the end node, get all of its neighbors that aren't checked or
        // in the queue
        for (Node nextNode : neighbors) {
          if (!checked.contains(nextNode) && !queue.contains(nextNode)) {
            queue.add(nextNode);
            cameFrom.put(nextNode.ID, currNode.ID);
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
