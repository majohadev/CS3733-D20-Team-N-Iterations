package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import edu.wpi.N.entities.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import org.bridj.util.Pair;

public abstract class AbsAlgo implements IPathFinder {

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
   * Helper function which generates Path given a Map
   *
   * @param cameFrom: Map, where key: NodeID, value: came-from-NodeID
   * @return Path object containing generated path
   */
  static Path generatePath(Node start, Node end, Map<String, String> cameFrom) {
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
  public Path findQuickAccess(DbNode start, String nodeType) throws DBException {
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
        //        Algorithm thePathFinder = new Algorithm();

        return findPath(start, end, false);
      } else {
        return null;
      }
    } catch (DBException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Finds the best stairs to take based on score
   *
   * @param startNode: Starting Node
   * @param endNode: End Node
   * @return: Stair node and its score
   * @throws DBException
   */
  private static DbNode getBestStopQuickAccess(DbNode startNode, DbNode endNode, String nodeType)
      throws DBException {
    LinkedList<DbNode> startFloorNodes =
        MapDB.searchNode(startNode.getFloor(), startNode.getBuilding(), nodeType, "");
    if (startNode.getFloor() != endNode.getFloor()) {
      LinkedList<DbNode> endFloorNodes =
          MapDB.searchNode(endNode.getFloor(), endNode.getBuilding(), nodeType, "");
      startFloorNodes.addAll(endFloorNodes);
    }

    DbNode lowestSoFar = null;
    double score = 1000000;
    for (DbNode currentNode : startFloorNodes) {
      Node GNode = MapDB.getGNode(currentNode.getNodeID());
      double currentCost = cost(MapDB.getGNode(startNode.getNodeID()), GNode);
      double currentScore = currentCost + heuristic(GNode, MapDB.getGNode(endNode.getNodeID()));
      if (currentScore < score) {
        lowestSoFar = currentNode;
        score = currentScore;
      }
    }
    return lowestSoFar;
  }

  /**
   * Finds the best path from the start to the stop node given, then the best path from the stop to
   * the end
   *
   * @param start: Start node
   * @param end: End node
   * @param stop: Stop node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: A pair of paths (start to stop, stop to end)
   * @throws DBException
   */
  public Pair<Path, Path> getPathWithStop(DbNode start, DbNode end, DbNode stop, boolean handicap)
      throws DBException {
    Path pathOne = findPath(start, stop, handicap);
    Path pathTwo = findPath(stop, end, handicap);
    return new Pair<>(pathOne, pathTwo);
  }

  /**
   * Finds the best path from the start to a stop node of the type given, then the best path from
   * the stop to the end
   *
   * @param start: Start node
   * @param end: End node
   * @param nodeType: nodeType desired for the stop node
   * @param handicap: Boolean saying whether path should be handicap accessible only
   * @return: A pair of paths (start to stop, stop to end)
   * @throws DBException
   */
  public Pair<Path, Path> getPathWithStop(
      DbNode start, DbNode end, String nodeType, boolean handicap) throws DBException {
    DbNode stop = getBestStopQuickAccess(start, end, nodeType);
    Path pathOne = findPath(start, stop, handicap);
    Path pathTwo = findPath(stop, end, handicap);
    return new Pair<>(pathOne, pathTwo);
  }

  /**
   * Returns a Linked list of DbNode arrays, each array containing two nodes indicating an edge
   * between floors for the elevator or staircase indicated by the given node
   *
   * @param node, from the elevator or staircase you want edges for
   * @return LinkedList<DbNode []>
   * @throws DBException
   */
  public static LinkedList<DbNode[]> getEdgesBetweenFloors(DbNode node) throws DBException {
    LinkedList<DbNode[]> edges = new LinkedList<>();
    if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) {
      return null;
    }
    LinkedList<DbNode> floorchangeNodes = new LinkedList<DbNode>();
    for (int i = 1;
        i <= 5;
        i++) { // will need to change when we add another building with different number of floors
      floorchangeNodes.addAll(MapDB.searchNode(i, node.getBuilding(), node.getNodeType(), ""));
    }
    ArrayList<DbNode> thisFloorChangeNodes = new ArrayList<DbNode>();
    for (int i = 0; i < 5; i++) {
      thisFloorChangeNodes.add(
          i, new DbNode("1234567890", 0, 0, -1, "MainBuil", "HALL", "Hall 1", "Hall 1", 'N'));
    }
    for (DbNode n : floorchangeNodes) {
      if (node.getX() == n.getX() && node.getY() == n.getY()) {
        thisFloorChangeNodes.add(n.getFloor() - 1, n);
      }
    }
    for (DbNode fCNOde : thisFloorChangeNodes) {
      for (DbNode adj : MapDB.getAdjacent(fCNOde.getNodeID())) {
        if (fCNOde.getNodeType().equals(adj.getNodeType()) && adj.getFloor() > fCNOde.getFloor()) {
          DbNode[] nodes = new DbNode[] {fCNOde, adj};
          edges.add(nodes);
        }
      }
    }
    return edges;
  }
}
