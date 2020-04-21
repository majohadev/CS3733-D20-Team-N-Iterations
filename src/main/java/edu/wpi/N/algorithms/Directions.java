package edu.wpi.N.algorithms;

import static edu.wpi.N.algorithms.Directions.State.*;
import static java.lang.Math.atan2;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.DbController;
import edu.wpi.N.entities.DbNode;
import java.util.ArrayList;
import java.util.LinkedList;

public class Directions {
  private ArrayList<String> directions;
  private static LinkedList<DbNode> path;
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
    this.path = path;
  }

  /** Generates textual directions for the given path */
  private void generateDirections() throws DBException {
    DbNode currNode;
    DbNode nextNode = path.getFirst();
    double distance = 0;
    boolean stateChange = true;
    double angle = 0;
    String message = "";
    boolean r = true;
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
          if (!path.getFirst().getNodeType().equals("HALL")) {
            message =
                "Start by exiting "
                    + path.getFirst().getLongName()
                    + " "; // sometimes this produces an extra space
          } else if (!(getLandmark(nextNode) == null)) {
            message =
                "Start towards "
                    + getLandmark(nextNode).getLongName()
                    + getDistanceString(getDistance(currNode, nextNode));
          } else {
            message =
                "Start by proceeding down the corridor"
                    + getDistanceString(getDistance(currNode, nextNode));
          }
          break;
        case EXITING: // not implemented yet, for building change or elevators
          directions.add("Exit " + currNode.getLongName());
          break;
        case CONTINUING: // could add if passing an intersection, "continue past " + landmark
          distance += getDistance(currNode, nextNode);
          if (!message.equals("")) {
            if (atEndOfHall(nextNode)) {
              directions.add(message + "and proceed to the end of the hallway");
            } else {
              directions.add(message + "and proceed down the hallway");
            }
            message = "";
          } else if (stateChange || atIntersection(currNode)) {
            if (getLandmark(nextNode) == null) {
              message = "Continue to next intersection" + getDistanceString(distance);
            } else if (getLandmark(nextNode).equals(nextNode)) {
              message =
                  "Proceed straight towards "
                      + getLandmark(nextNode).getLongName()
                      + getDistanceString(distance);
            } else if (atEndOfHall(nextNode)) {
              message = "Continue to the end of the hallway" + getDistanceString(distance);
            } else {
              message =
                  "Continue past "
                      + getLandmark(currNode).getLongName()
                      + getDistanceString(distance);
            }
            distance = 0;
          }
          break;
        case TURNING:
          if (!nextNode.equals(path.get(path.size() - 1))) {
            if (!message.equals("")) {
              if (i == 1) {
                directions.add(message + "and turning " + getTurnType(angle, getAngle(i - 1)));
              } else {
                directions.add(message + "and turn " + getTurnType(angle, getAngle(i - 1)));
              }
              message = "";
            } else if (!(getLandmark(currNode) == null)) {
              directions.add(
                  "Go straight towards "
                      + getLandmark(currNode).getLongName()
                      + getDistanceString(getDistance(currNode, nextNode))
                      + "and turn "
                      + getTurnType(angle, getAngle(i - 1))
                      + " at the next intersection");
            } else {
              directions.add(
                  "Proceed to next intersection"
                      + getDistanceString(getDistance(currNode, nextNode))
                      + "and turn "
                      + getTurnType(angle, getAngle(i - 1)));
            }
          }
          break;
        case CHANGING_FLOOR: // not implemented yet
          if (stateChange) {
            directions.add(getFloorChangeString(nextNode));
          }
          break;
        case ARRIVING:
          if (getState(i - 1).equals(TURNING)) {
            String turnMessage = "Turn " + getTurnType(angle, getAngle(i - 2));
            directions.add(turnMessage + " and arrive at " + currNode.getLongName());
          } else if (!message.equals("")) {
            directions.add(message + "and arrive at destination");
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
    } else if (Math.abs(getAngle(i) - getAngle(i - 1)) >= TURN_THRESHOLD) {
      // System.out.println(Math.abs(getAngle(i) - getAngle(i - 1)));
      return TURNING;
    } else if (path.get(i).getFloor() != path.get(i + 1).getFloor()) {
      return CHANGING_FLOOR;
    } else if (!path.get(i).getBuilding().equals(path.get(i + 1).getBuilding())) {
      return EXITING;
    } else {
      // System.out.println(Math.abs(getAngle(i) - getAngle(i - 1)));
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
    if (angleChange > 180) {
      angleChange -= 360;
    } else if (angleChange < -180) {
      angleChange += 360;
    }
    if (angleChange >= TURN_THRESHOLD) {
      return "right";
    } else if (angleChange <= -1 * TURN_THRESHOLD) {
      return "left";
    } else if (angleChange < TURN_THRESHOLD
        && angleChange > -TURN_THRESHOLD) { // eventually will add slight turns
      return "straight";
    } else {
      return "other turn type";
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
      for (DbNode n : DbController.getAdjacent(node.getNodeID())) {
        if (!n.getNodeType().equals("HALL")) {
          return n;
        }
      }
      return null;
    }
    return node; // change to exclude hallway nodes etc.
  }

  /**
   * Generates string (Go X ft) rounds distance to whole number
   *
   * @param distance, end, dbNode start and end
   * @return String, how far in feet between nodes
   */
  private static String getDistanceString(double distance) {
    return " (Go " + Math.round(distance) + " ft) ";
  }

  /**
   * generates a string "Take ___ to floor __" using floor number and nodeType
   *
   * @param node, end node of floor change
   * @return String
   */
  private static String getFloorChangeString(DbNode node) {
    String direction = "Take ";
    if (node.getNodeType().equals("ELEV")) {
      direction += "elevator to floor " + node.getFloor();
    } else if (node.getNodeType().equals("STAI")) {
      direction += "stairs to floor " + node.getFloor();
    }
    return direction;
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
   * @param node
   * @return true, if intersection, false otherwise
   */
  private static boolean atIntersection(DbNode node) throws DBException {
    int hallNodeCount = 0;
    if (node.getNodeType().equals("HALL")) {
      for (DbNode n : DbController.getAdjacent(node.getNodeID())) {
        if (n.getNodeType().equals("HALL")) {
          hallNodeCount++;
        }
      }
      if (hallNodeCount >= 3) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if a node is the last node in a hallway
   *
   * @param node
   * @return true, if the node is at the end of the hall, false otherwise
   */
  private static boolean atEndOfHall(DbNode node) throws DBException {
    int hallNodeCount = 0;
    if (node.getNodeType().equals("HALL")) {
      for (DbNode n : DbController.getAdjacent(node.getNodeID())) {
        if (n.getNodeType().equals("HALL")) {
          hallNodeCount++;
        }
      }
      if (hallNodeCount <= 1) {
        return true;
      }
    }
    return false;
  }

  /**
   * Takes a path and returns written directions for that path
   *
   * @return ArrayList of strings, each String is a line of directions
   */
  public ArrayList<String> getDirections() throws DBException {
    this.generateDirections();
    return this.directions;
  }
}
