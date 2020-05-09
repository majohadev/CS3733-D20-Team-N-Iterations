package edu.wpi.N.algorithms;

import static edu.wpi.N.algorithms.Directions.State.*;
import static java.lang.Math.atan2;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.MapDB;
import edu.wpi.N.entities.DbNode;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Directions {
  private ArrayList<String> directions;
  private static ArrayList<DbNode> path;
  private static State state;
  private static final double TURN_THRESHOLD = 45;
  private static final double SLIGHT_TURN_THRESHOLD = 20;
  private static final double SHARP_TURN_THRESHOLD = 95;
  private static LinkedList<DbNode> entranceNodes;

  enum State {
    STARTING,
    EXITING,
    CONTINUING,
    TURNING,
    CHANGING_FLOOR,
    ARRIVING
  }

  public Directions(LinkedList<DbNode> path) {
    // this.entranceNodes.add();
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
    DbNode endOfHallNode = null;
    double distance = 0;
    boolean stateChange = true;
    double angle = 0;
    String startFloor = "";
    String message = "";
    boolean messageCheck = false;
    double totalDistance = 0;
    double totalTime = 0;
    for (int i = 0; i <= path.size() - 1; i++) {
      currNode = path.get(i);
      if (i < path.size() - 1) {
        nextNode = path.get(i + 1);
        angle = getAngle(i);
        stateChange = !getState(i + 1).equals(state);
        totalDistance += getDistance(path.get(i), path.get(i + 1));
      }
      state = getState(i);
      // System.out.println(state);
      switch (state) {
        case STARTING:
          if (currNode.getNodeType().equals("EXIT")) {
            message = "Enter at " + getLandmark(currNode).getLongName();
          } else if (currNode.getNodeID().equals("NSERV00301")
              || currNode.getNodeID().equals("NSERV00103")) {
            message = "Start in the direction of the kiosk arrow ";
          } else if (!path.get(0).getNodeType().equals("HALL")) {
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
        case CONTINUING:
          distance += getDistance(currNode, nextNode);
          //          if (endOfHallNode == null) {
          //            endOfHallNode = findEndOfHall(i);
          //          }
          if (!message.equals("") && i == 1) { // TODO:NEW
            directions.add(message);
            message = "";
          } else if (getState(i - 1).equals(CHANGING_FLOOR)) {
            message = "Exit " + currNode.getLongName();
          } else if (stateChange || atIntersection(currNode)) {
            if (getLandmark(nextNode) == null) {
              if (currNode.getBuilding().equals("Faulkner"))
                message = "Continue to next intersection " + getDistanceString(distance);

            } else if (getLandmark(nextNode).equals(nextNode)) {
              message =
                  "Go towards " // "Proceed straight towards "
                      + getLandmark(nextNode).getLongName()
                      + " "
                      + getDistanceString(distance);
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
              directions.add(message + " and t" + getTurnType(angle, getAngle(i - 1)));
              message = "";
            } else if (!(getLandmark(currNode) == null)) {
              directions.add(
                  "Go towards " // "Go straight towards "
                      + getLandmark(currNode).getLongName()
                      + " "
                      + getDistanceString(getDistance(currNode, nextNode))
                      + " and t"
                      + getTurnType(angle, getAngle(i - 1))
                      + " at the intersection");
            } else {
              if (distance == 0) {
                directions.add(
                    "At the next intersection "
                        + getDistanceString(getDistance(currNode, nextNode))
                        + " t"
                        + getTurnType(angle, getAngle(i - 1)));
              } else {
                directions.add(
                    "At the next intersection "
                        + getDistanceString(distance)
                        + " t"
                        + getTurnType(angle, getAngle(i - 1)));
              }
            }
          }
          break;
        case CHANGING_FLOOR:
          totalTime += 37; // add 37 sec for average floor change time
          if (!getState(i - 1).equals(CHANGING_FLOOR)) {
            startFloor = currNode.getLongName();
          }
          if (!message.equals("")) {
            directions.add(message + " and enter " + currNode.getLongName());
            message = "";
            messageCheck = true;
          }
          if (stateChange && getState(i - 1).equals(CHANGING_FLOOR)) {
            if (messageCheck) {
              directions.add("Take " + startFloor + " to floor " + currNode.getFloor());
              messageCheck = false;
            } else {
              directions.add("Enter " + startFloor + " and go to floor " + currNode.getFloor());
            }
          }
          break;
        case ARRIVING:
          if (currNode.getNodeType().equals("EXIT")) {
            if (!message.equals("")) {
              directions.add(message + " and exit at " + currNode.getLongName());
              message = "";
            } else directions.add("Exit at " + currNode.getLongName());

          } else if (getState(i - 1).equals(TURNING)) {
            String turnMessage = "T" + getTurnType(angle, getAngle(i - 2));
            directions.add(
                turnMessage
                    + " and arrive at "
                    + currNode.getLongName()
                    + " "
                    + getTotalTimeString(totalDistance, totalTime));
          } else if (!message.equals("")) {
            directions.add(
                message
                    + " and arrive at destination "
                    + getTotalTimeString(totalDistance, totalTime));
            message = "";
          } else if (currNode.getNodeType().equals("EXIT")) {
            directions.add("Exit " + currNode.getLongName());
          } else {
            directions.add(
                "Arrive at "
                    + currNode.getLongName()
                    + " "
                    + getTotalTimeString(totalDistance, totalTime));
          }
          break;
      }
    }
  }

  /**
   * gets string for total time using average walking speed of 4.6 ft/s and avg elevator ride of 37
   * sec
   *
   * @param totalDistance
   * @param time
   * @return String
   */
  public static String getTotalTimeString(double totalDistance, double time) {
    int totalTime = (int) Math.round((totalDistance / 4.6 + time) / 60);
    if (totalTime <= 0) {
      return "(Estimated time less than 1 minute)";
    } else if (totalTime == 1) {
      return "(Estimated time " + totalTime + " minute)";
    } else {
      return "(Estimated time " + totalTime + " minutes)";
    }
  }

  private static DbNode findEndOfHall(int index) throws DBException {
    double angleChange;
    boolean endOfHall = false;
    while (getState(index).equals(CONTINUING)
        && index < path.size()) { // || (getState(index - 1).equals(CONTINUING)
      for (DbNode adj : MapDB.getAdjacent(path.get(index).getNodeID())) {
        if (adj.getNodeType().equals("HALL")) {
          angleChange = getAngle(index, adj) - getAngle(index - 1);
          if (angleChange > 180) {
            angleChange -= 360;
          } else if (angleChange < -180) {
            angleChange += 360;
          }
          if (Math.abs(angleChange) < SLIGHT_TURN_THRESHOLD && !adj.equals(path.get(index - 1))) {
            endOfHall = false;
            break;
          } else {
            endOfHall = true;
          }
        }
      }
      if (endOfHall) {
        // System.out.println(path.get(index));
        return path.get(index);
      }
      index++;
    }
    return null;
  }

  /**
   * gets the state based off of currNode and change in angle
   *
   * @return int, State
   */
  private static State getState(int i) {

    if (i == 0) {
      return STARTING;
    } else if (i == path.size() - 1) {
      return ARRIVING;
    } else if ((path.get(i).getNodeType().equals("ELEV")
            || path.get(i).getNodeType().equals("STAI"))
        && (path.get(i).getFloor() != path.get(i + 1).getFloor()
            || path.get(i).getNodeType().equals(path.get(i - 1).getNodeType()))) {
      return CHANGING_FLOOR;
    } else if (Math.abs(getAngle(i) - getAngle(i - 1)) > SLIGHT_TURN_THRESHOLD
        && Math.abs(getAngle(i) - getAngle(i - 1)) < 360 - SLIGHT_TURN_THRESHOLD) {
      return TURNING;
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
    if (angleChange > 180) {
      angleChange -= 360;
    } else if (angleChange < -180) {
      angleChange += 360;
    }
    // System.out.println(angleChange);
    if (angleChange <= TURN_THRESHOLD && angleChange >= SLIGHT_TURN_THRESHOLD) {
      return "ake a slight right";
    } else if (angleChange > SHARP_TURN_THRESHOLD) {
      return "ake a sharp right turn";
    } else if (angleChange >= TURN_THRESHOLD) {
      return "urn right";
    } else if (angleChange >= -1 * TURN_THRESHOLD && angleChange <= -1 * SLIGHT_TURN_THRESHOLD) {
      return "ake a slight left";
    } else if (angleChange <= -1 * SHARP_TURN_THRESHOLD) {
      return "ake a sharp left turn";
    } else if (angleChange <= -1 * TURN_THRESHOLD) {
      return "urn left";
    } else {
      return "straight" + angleChange;
    }
  }

  /**
   * Gets closest landmark to a node, if the node is a HALL node, returns an adjacent non-hallway
   * node or null if node is not a HALL node, returns the same node
   *
   * @param node, DbNode
   * @return String, landmark for given node
   */
  private static DbNode getLandmark(DbNode node) throws DBException {
    if (node.getNodeType().equals("HALL")) {
      for (DbNode n : MapDB.getAdjacent(node.getNodeID())) {
        if (!n.getNodeType().equals("HALL")
            && !n.getNodeType().equals("ELEV")
            && !n.getNodeType().equals("STAI")) {
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
   * gets the angle between two nodes use atan2
   *
   * @return double, angle
   */
  private static double getAngle(int i, DbNode n) {
    double dy = n.getY() - path.get(i).getY();
    double dx = n.getX() - path.get(i).getX();
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
    double conversion = 0.338;
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
  public ArrayList<String> getNumberedDirection() {
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

  /**
   * gets the google directions with the specified mode and direction
   *
   * @param mode walking, driving,, bycycling, transit; gets directions in one of those formats
   * @param dir the direction. True = from 45 francis street to 1153 centre street, false = opposite
   *     direction
   * @return The google directions as a string
   */
  public static ArrayList<String> getGoogleDirectionsStrings(String mode, boolean dir) {
    String urls;
    if (dir) {
      urls =
          "https://maps.googleapis.com/maps/api/directions/json?mode="
              + mode
              + "&origin=45|Francis|Street,|Boston,|MA,"
              + "&destination=1153|Centre|St,|Boston,|MA"
              + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";
    } else {
      urls =
          "https://maps.googleapis.com/maps/api/directions/json?mode="
              + mode
              + "&origin=1153|Centre|St,|Boston,|MA"
              + "&destination=45|Francis|Street,|Boston,|MA,"
              + "&key=AIzaSyDx7BSweq5dRzXavs1vxuMWeR2ETMR6b3Q";
    }
    try {
      URL url = new URL(urls);
      HttpURLConnection httpcon = (HttpURLConnection) (url.openConnection());
      httpcon.setDoOutput(true);
      httpcon.setRequestProperty("Content-Type", "application/json");
      httpcon.setRequestProperty("Accept", "application/json");
      httpcon.setRequestMethod("GET");
      httpcon.connect();
      Scanner sc = new Scanner(url.openStream());
      ArrayList<String> dirs = new ArrayList<>();
      while (sc.hasNext()) {
        String next = sc.nextLine();
        // if (next.contains("\"html_instructions\"")) System.out.println(next);
        if (next.contains("\"html_instructions\""))
          dirs.add(
              next.substring(44)
                  .replace("\\u003cb\\u003e", "")
                  .replace("\\u003c/b\\u003e", "")
                  .replace("\\u003cwbr/\\u003e", "\n")
                  .replace("&nbsp;", " ")
                  .replaceAll("(\\\\u003c)(.*?)(\\\\u003e)", "\n")
                  .replace("\",", ""));
        // System.out.println(sc.nextLine() + "K");
      }
      return dirs;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
/*
  public static ArrayList<Direction> getGoogleDirections(String mode, boolean dir){
    ArrayList<String> directions = new ArrayList<>();
    ArrayList<Direction> iconDirections = new ArrayList<>();
    for(String dir: directions){
      if(dir.contains("right")){
        iconDirections.add()
      }
    }
  }*/
}
