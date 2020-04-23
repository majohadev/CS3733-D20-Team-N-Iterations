package edu.wpi.N.algorithms;

import static edu.wpi.N.algorithms.Directions.State.*;
import static java.lang.Math.atan2;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.util.ArrayList;
import java.util.LinkedList;

public class Directions {
  private ArrayList<String> directions;
  private static ArrayList<DbNode> path;
  private static State state;
  private static final double TURN_THRESHOLD = 30;

  enum State {
    STARTING,
    EXITING,
    CONTINUING,
    TURNING,
    CHANGING_FLOOR,
    ARRIVING
  }

  public Directions(LinkedList<DbNode> path) {
    this.directions = new ArrayList<>();
    ArrayList<DbNode> pathNodes = new ArrayList<DbNode>();
    for (DbNode node : path) {
      pathNodes.add(node);
    }
    this.path = pathNodes;
  }

  /** Generates textual directions for the given path */
  private void generateDirections() throws DBException {
    DbNode currNode;
    DbNode nextNode = path.get(0);
    double distance = 0;
    boolean stateChange = true;
    double angle = 0;
    String startFloor = "";
    String message = "";
    boolean spagetti = false;
    for (int i = 0; i <= path.size() - 1; i++) {
      currNode = path.get(i);
      if (i < path.size() - 1) {
        nextNode = path.get(i + 1);
        angle = getAngle(i);
        stateChange = !getState(i + 1).equals(state);
      }
      state = getState(i);
      switch (state) {
        case STARTING:
          if (!path.get(0).getNodeType().equals("HALL")) {
            message = "Exit " + path.get(0).getLongName(); // "Start by exiting "
          } else if (!(getLandmark(nextNode) == null)) {
            message =
                "Start towards "
                    + getLandmark(nextNode).getLongName()
                    + " "
                    + getDistanceString(getDistance(currNode, nextNode));
          } else {
            message =
                "Start by proceeding down the hall "
                    + getDistanceString(getDistance(currNode, nextNode));
          }
          break;
        case EXITING: // not implemented yet, for building change
          directions.add("Exit " + currNode.getLongName());
          break;
        case CONTINUING:
          distance += getDistance(currNode, nextNode);
          if (!message.equals("")) {
            directions.add(message); // + " and proceed down the hall");
            message = "";
          } else if (stateChange || atIntersection(currNode)) {
            if (getLandmark(nextNode) == null) {
              message = "Continue to next intersection " + getDistanceString(distance);
            } else if (getLandmark(nextNode).equals(nextNode)) {
              message =
                  "Go towards " // "Proceed straight towards "
                      + getLandmark(nextNode).getLongName()
                      + " "
                      + getDistanceString(distance);
              // } else if (atEndOfHall(nextNode)) { //this should go before first if
              // message = "Continue to the end of the hallway " + getDistanceString(distance);
            } else {
              message =
                  "Continue past "
                      + getLandmark(nextNode).getLongName()
                      + " "
                      + getDistanceString(distance);
            }
            distance = 0;
          }
          break;
        case TURNING:
          if (!nextNode.equals(path.get(path.size() - 1))) {
            if (!message.equals("")) {
              directions.add(message + " and turn " + getTurnType(angle, getAngle(i - 1)));
              message = "";
            } else if (!(getLandmark(currNode) == null)) {
              directions.add(
                  "Go towards " // "Go straight towards "
                      + getLandmark(currNode).getLongName()
                      + " "
                      + getDistanceString(getDistance(currNode, nextNode))
                      + " and turn "
                      + getTurnType(angle, getAngle(i - 1))
                      + " at the intersection");
            } else {
              directions.add(
                  "Proceed to next intersection "
                      + getDistanceString(getDistance(currNode, nextNode))
                      + " and turn "
                      + getTurnType(angle, getAngle(i - 1)));
            }
          }
          break;
        case CHANGING_FLOOR:
          if (!getState(i - 1).equals(CHANGING_FLOOR)) {
            startFloor = currNode.getLongName();
          }
          if (!message.equals("")) {
            directions.add(message + " and enter " + currNode.getLongName());
            message = "";
            spagetti = true;
          }
          if (stateChange) {
            if (spagetti) {
              directions.add("Take " + startFloor + " to floor " + currNode.getFloor());
              spagetti = false;
            } else {
              directions.add("Enter " + startFloor + " and go to floor " + currNode.getFloor());
            }
          }
          break;
        case ARRIVING:
          if (getState(i - 1).equals(TURNING)) {
            String turnMessage = "Turn " + getTurnType(angle, getAngle(i - 2));
            directions.add(turnMessage + " and arrive at " + currNode.getLongName());
          } else if (!message.equals("")) {
            directions.add(message + " and arrive at destination");
          } else {
            directions.add("Arrive at destination");
          }
          break;
      }
    }
  }

  /**
   * gets the state based off of currNode and nextNode
   *
   * @return int, State
   */
  private static State getState(int i) {

    if (i == 0) {
      return STARTING;
    } else if (i == path.size() - 1) {
      return ARRIVING;
    } else if (Math.abs(getAngle(i) - getAngle(i - 1)) > TURN_THRESHOLD
        && Math.abs(getAngle(i) - getAngle(i - 1)) < 360 - TURN_THRESHOLD) {
      return TURNING;
    } else if ((path.get(i).getNodeType().equals("ELEV")
            || path.get(i).getNodeType().equals("STAI"))
        && (path.get(i).getFloor() != path.get(i + 1).getFloor()
            || path.get(i).getNodeType().equals(path.get(i - 1).getNodeType()))) {
      return CHANGING_FLOOR;
    } else if (!path.get(i).getBuilding().equals(path.get(i + 1).getBuilding())) {
      return EXITING;
    } else {
      return CONTINUING;
    }
  }

  /**
   * Takes a current node and next node and returns the type of turn
   *
   * @param angle, double change in angle
   * @return String, the type of turn (right, left, straight)
   */
  private static String getTurnType(double angle, double prevAngle) {
    double angleChange = angle - prevAngle;
    if (angleChange > 175) {
      angleChange -= 360;
    } else if (angleChange < -175) {
      angleChange += 360;
    }
    if (angleChange >= TURN_THRESHOLD) {
      return "right";
    } else if (angleChange <= -1 * TURN_THRESHOLD) {
      return "left";
    } else {
      return "straight" + angleChange;
    }
  }

  /**
   * Gets closest landmark to a node (ie. won't return hallway nodes...or returning hallway nodes->
   * "end of corridor" or something?)
   *
   * @param node, DbNode
   * @return String, landmark for given node
   */
  private static DbNode getLandmark(DbNode node) throws DBException {
    if (node.getNodeType().equals("HALL")) {
      for (DbNode n : MapDB.getAdjacent(node.getNodeID())) {
        if (!n.getNodeType().equals("HALL")) {
          return n;
        }
      }
      return null;
    }
    return node;
  }

  /**
   * Generates string (Go X ft) rounds distance to whole number
   *
   * @param distance, end, dbNode start and end
   * @return String, how far in feet between nodes
   */
  private static String getDistanceString(double distance) {
    return "(" + Math.round(distance) + " ft)"; // "(Go " + Math.round(distance) + " ft)"
  }

  /**
   * gets the angle between two nodes use atan2
   *
   * @return double, angle
   */
  private static double getAngle(int i) {
    double dy = path.get(i + 1).getY() - path.get(i).getY();
    double dx = path.get(i + 1).getX() - path.get(i).getX();
    return Math.toDegrees(atan2(dy, dx));
  }

  /**
   * calculates the distance between two nodes using appropriate conversion factor
   *
   * @param currNode, current DbNode
   * @param nextNode, next DbNode
   * @return double, distance between current node and next node
   */
  private static double getDistance(DbNode currNode, DbNode nextNode) {
    double distance =
        Math.sqrt(
            Math.pow(nextNode.getX() - currNode.getX(), 2)
                + Math.pow(nextNode.getY() - currNode.getY(), 2));
    double conversion = 1;
    return distance * conversion;
  }

  /**
   * Determines if a node is at an intersection by looking for adjacent hallway nodes
   *
   * @param node, DbNode
   * @return true, if intersection, false otherwise
   */
  private static boolean atIntersection(DbNode node) throws DBException {
    int hallNodeCount = 0;
    if (node.getNodeType().equals("HALL")) {
      for (DbNode n : MapDB.getAdjacent(node.getNodeID())) {
        if (n.getNodeType().equals("HALL")) {
          hallNodeCount++;
        }
      }
      return hallNodeCount >= 3;
    }
    return false;
  }

  /** @return directions with numbers at beginning of each line */
  private ArrayList<String> getNumberedDirection() {
    ArrayList<String> newDirections = new ArrayList<>();
    int index = 1;
    if (!this.directions.isEmpty()) {
      for (String s : this.directions) {
        newDirections.add(index + ". " + s);
        index++;
      }
    } else {
      return null;
    }
    return newDirections;
  }

  /**
   * Takes a path and returns written directions for that path
   *
   * @return ArrayList of strings, each String is a line of directions
   */
  public ArrayList<String> getDirections() throws DBException {
    if (!(this.path == null)) {
      this.generateDirections();
      return this.getNumberedDirection();
    } else {
      return null;
    }
  }

  /*    */
  /**
   * Determines if a node is the last node in a hallway
   *
   * @param node, DbNode
   * @return true, if the node is at the end of the hall, false otherwise
   */
  /*
  private static boolean atEndOfHall(DbNode node) throws DBException {
      int hallNodeCount = 0;
      if (node.getNodeType().equals("HALL")) {
          for (DbNode n : MapDB.getAdjacent(node.getNodeID())) {
              if (n.getNodeType().equals("HALL")) {
                  hallNodeCount++;
              }
          }
        return hallNodeCount <= 1;
      }
      return false;
  }*/
}
