package edu.wpi.N.algorithms;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Path;
import java.util.*;
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
  public static double cost(DbNode currNode, DbNode nextNode) {
    return Math.sqrt(
        Math.pow(nextNode.getX() - currNode.getX(), 2)
            + Math.pow(nextNode.getY() - currNode.getY(), 2));
  }

  /**
   * Function calculates Manhattan distance between goal and current Node
   *
   * @param currNode: current Node
   * @return Manhattan distance to the goal Node
   */
  public static double heuristic(DbNode currNode, DbNode end) {
    return Math.abs(end.getX() - currNode.getX()) + Math.abs(end.getY() - currNode.getY());
  }

  /**
   * Helper function which generates Path given a Map
   *
   * @param cameFrom: Map, where key: NodeID, value: came-from-NodeID
   * @return Path object containing generated path
   */
  static Path generatePath(DbNode start, DbNode end, Map<String, String> cameFrom) {
    try {
      String currentID = end.getNodeID();
      LinkedList<DbNode> path = new LinkedList<DbNode>();
      path.add(MapDB.getNode(currentID));

      try {
        while (!currentID.equals(start.getNodeID())) {
          currentID = cameFrom.get(currentID);
          path.addFirst(MapDB.getNode(currentID));
        }
      } catch (NullPointerException e) {
        System.out.println("Location was not found.");
        return null;
      }

      Path finalPath = new Path(path);

      return finalPath;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Finds the closest node of a given type and returns a path to it
   *
   * @param start, DbNode of starting node
   * @param nodeType, String the type of node you want (must be length 4)
   * @return Path, path from start node to closest (eucledian) end node of requested type
   */
  public Path findQuickAccess(
      HashMap<String, LinkedList<DbNode>> mapData, DbNode start, String nodeType) {
    try {
      LinkedList<DbNode> nodes =
          MapDB.searchNode(start.getFloor(), start.getBuilding(), nodeType, "");
      if (!nodes.isEmpty()) {
        double closest = cost(start, nodes.getFirst());
        DbNode end = nodes.getFirst();
        for (DbNode n : nodes) {
          double cost = cost(start, n);
          if (cost <= closest) {
            closest = cost;
            end = n;
          }
        }

        return findPath(mapData, start, end, false);
      } else {
        return null;
      }
    } catch (DBException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Finds a stop node of a given type that is closest to the start node using nodes from the start and end floors
   *
   * @param startNode: Starting Node
   * @param endNode: End Node
   * @param nodeType: Node type to stop at
   * @return: DbNode to stop of the given the node type
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

      double currentCost = cost(startNode, currentNode);
      double currentScore = currentCost + heuristic(currentNode, endNode);
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
   */
  public Pair<Path, Path> getPathWithStop(
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode start,
      DbNode end,
      DbNode stop,
      boolean handicap) {
    Path pathOne = findPath(mapData, start, stop, handicap);
    Path pathTwo = findPath(mapData, stop, end, handicap);
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
      HashMap<String, LinkedList<DbNode>> mapData,
      DbNode start,
      DbNode end,
      String nodeType,
      boolean handicap)
      throws DBException {
    DbNode stop = getBestStopQuickAccess(start, end, nodeType);
    Path pathOne = findPath(mapData, start, stop, handicap);
    Path pathTwo = findPath(mapData, stop, end, handicap);
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

  private static class FloorSortNodes implements Comparator<DbNode> {

    @Override
    public int compare(DbNode o1, DbNode o2) {
      return o1.getFloor() - o2.getFloor();
    }
  }

  /**
   * Gets all of the elevator and stair nodes accessible to the given node. If the given node isn't
   * an elevator or stair, returns null.
   *
   * @param node The node you want to search for accessible stairs/elevators from
   * @return A linked list of DbNodes that are connected directly or indirectly to the node
   */
  public static LinkedList<DbNode> searchAccessible(DbNode node) {
    if (!(node.getNodeType().equals("ELEV") || node.getNodeType().equals("STAI"))) return null;
    Queue<DbNode> queue = new LinkedList<DbNode>(); // queue for breadth-first search
    queue.add(node);
    LinkedList<DbNode> nodes = new LinkedList<DbNode>();
    while (queue.size() > 0) {
      DbNode cur = queue.poll();
      nodes.add(cur);
      LinkedList<DbNode> next;
      try {
        next =
            MapDB.getAdjacent(
                cur.getNodeID(),
                -1,
                -1); // only gets elevator and stair nodes; kinda a hack, but not actually that bad
        // due to the sql construction
      } catch (DBException e) {
        System.out.println(e.getMessage());
        continue; // skip invalid nodes
      }
      Iterator<DbNode> nextIt = next.iterator();
      while (nextIt.hasNext()) {
        DbNode n = nextIt.next();
        if (!(queue.contains(n) || nodes.contains(n))) queue.add(n);
      }
    }
    if (nodes.size() == 1) return null;
    Collections.sort(nodes, new FloorSortNodes());
    return nodes;
  }
}
