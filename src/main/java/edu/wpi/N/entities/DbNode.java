package edu.wpi.N.entities;

public class DbNode implements INode {
  private int x, y, floor;
  private String nodeID, building, nodeType, longName, shortName;
  private char teamAssigned;

  /**
   * Gets the x value of the node
   *
   * @return the x value of the node
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the floor of the node
   *
   * @return The node's floor
   */
  public int getFloor() {
    return floor;
  }

  /**
   * Gets the node's ID
   *
   * @return The ID of the node
   */
  public String getNodeID() {
    return nodeID;
  }

  /**
   * Gets the building of the node
   *
   * @return the Node's building
   */
  public String getBuilding() {
    return building;
  }

  /**
   * Gets the type of the node
   *
   * @return the node's type
   */
  public String getNodeType() {
    return nodeType;
  }

  /**
   * Gets the long name of the Node
   *
   * @return The node's long name
   */
  public String getLongName() {
    return longName;
  }

  /**
   * Gets the short name of the Node
   *
   * @return The node's short name
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Gets the team assigned to the node
   *
   * @return the team assigned to the node
   */
  public char getTeamAssigned() {
    return teamAssigned;
  }

  /**
   * Gets the Y value of the node
   *
   * @return the node's Y value
   */
  public int getY() {
    return y;
  }

  public DbNode(
      String nodeID,
      int x,
      int y,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      char teamAssigned) {
    this.nodeID = nodeID;
    this.x = x;
    this.y = y;
    this.floor = floor;
    this.building = building;
    this.nodeType = nodeType;
    this.longName = longName;
    this.shortName = shortName;
    this.teamAssigned = teamAssigned;
  }

  public DbNode() {
    this.nodeID = null;
    this.x = 0;
    this.y = 0;
    this.floor = 0;
    this.building = null;
    this.nodeType = null;
    this.longName = null;
    this.shortName = null;
    this.teamAssigned = '\0';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DbNode)) {
      return false;
    }

    DbNode other = (DbNode) o;

    return nodeID.equals(other.nodeID);
  }

  @Override
  public int hashCode() {
    // XOR values/multiply by prime num to mix them up
    return nodeID.hashCode() ^ ((x ^ y) * 17) ^ building.hashCode();
  }
}
