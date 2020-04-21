package edu.wpi.N.database;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class DbController {

  private static Statement statement;
  private static Connection con;

  public static Connection getCon() {
    return con;
  }
  /**
   * Adds a node to the database including the nodeID for importing from the CSV
   *
   * @param nodeID The node ID
   * @param x The x coordinate of the node
   * @param y The y coordinate of the node
   * @param floor The floor of the node
   * @param building The building the node is in
   * @param nodeType The node's type
   * @param longName The node's longName
   * @param shortName The node's shortName
   * @param teamAssigned The team assigned to the Node
   * @return True if valid and inserted properly, false otherwise.
   */
  // Noah
  public static boolean addNode(
      String nodeID,
      int x,
      int y,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      char teamAssigned)
      throws DBException {
    try {
      String query = "INSERT INTO nodes VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      stmt.setInt(2, x);
      stmt.setInt(3, y);
      stmt.setInt(4, floor);
      stmt.setString(5, building);
      stmt.setString(6, nodeType);
      stmt.setString(7, longName.replace("\'", "\\'"));
      stmt.setString(8, shortName.replace("\'", "\\'"));
      stmt.setString(9, String.valueOf(teamAssigned));
      stmt.executeUpdate();
      // System.out.println("Values Inserted");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addNode", e);
    }
  }

  /**
   * Modifies a Node in the database. May change NodeID
   *
   * @param nodeID the ID of the node you wish to change
   * @param x The new x coordinate of the node
   * @param y The new y coordinate of the node
   * @param floor The new floor of the node
   * @param building The new building the node is in
   * @param longName The new node's longName
   * @param shortName The new node's shortName
   * @param teamAssigned The new team assigned to the Node
   * @return true if the node was modified, false otherwise
   */
  // Noah
  public static boolean modifyNode(
      String nodeID,
      int x,
      int y,
      int floor,
      String building,
      String nodeType,
      String longName,
      String shortName,
      char teamAssigned)
      throws DBException {
    String newID;
    LinkedList<DbNode> edges = new LinkedList<DbNode>();
    String query;
    try {
      con.setAutoCommit(false);
      if (!(nodeID.substring(0, 5) + nodeID.substring(8))
          .equals(teamAssigned + nodeType.toUpperCase() + String.format("%02d", floor))) {
        if (nodeID.substring(1, 5).equals(nodeType)) {
          newID = teamAssigned + nodeID.substring(1, 8) + String.format("%02d", floor);
        } else {
          newID = teamAssigned + nodeType.toUpperCase() + nextAvailNum(nodeType) + "0" + floor;
        }
      } else newID = nodeID;
      if (!newID.equals(nodeID)) {
        edges = getAdjacent(nodeID);
        query = "DELETE FROM EDGES WHERE node1 = ? OR node2 = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, nodeID);
        stmt.setString(2, nodeID);
        stmt.executeUpdate();
      }
      query =
          "UPDATE nodes SET nodeID = ?, xcoord = ?, ycoord = ?, floor = ?, building = ?,"
              + " nodeType = ?, longName = ?, shortName = ?, teamAssigned = ? WHERE nodeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, newID);
      stmt.setInt(2, x);
      stmt.setInt(3, y);
      stmt.setInt(4, floor);
      stmt.setString(5, building);
      stmt.setString(6, nodeType);
      stmt.setString(7, longName.replace("\'", "\\'"));
      stmt.setString(8, shortName.replace("\'", "\\'"));
      stmt.setString(9, String.valueOf(teamAssigned));
      stmt.setString(10, nodeID);
      stmt.executeUpdate();
      Iterator<DbNode> it = edges.iterator();
      while (it.hasNext()) {
        addEdge(newID, it.next().getNodeID());
      }
      con.commit();
      con.setAutoCommit(true);
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new DBException("Unknown error: modifyNode", ex);
      }
      throw new DBException("Unknown error: modifyNode", e);
    }
  }

  /**
   * Modifies a Node in the database. Does not change the nodeID.
   *
   * @param nodeID the ID of the node you wish to change
   * @param x The new x coordinate of the node
   * @param y The new y coordinate of the node
   * @param longName The new node's longName
   * @param shortName The new node's shortName
   * @return true if the node was modified, false otherwise
   */
  public static boolean modifyNode(String nodeID, int x, int y, String longName, String shortName)
      throws DBException {
    try {
      String query =
          "UPDATE nodes SET xcoord = ?, ycoord = ?, longName = ?, shortName = ? WHERE nodeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);

      stmt.setInt(1, x);
      stmt.setInt(2, y);
      stmt.setString(3, longName);
      stmt.setString(4, shortName);
      stmt.setString(5, nodeID);

      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: modifyNode", e);
    }
  }

  /**
   * Moves a node to a new location
   *
   * @param nodeID the node ID of the node you wish to move
   * @param x The new x value that you want to move the node to
   * @param y The new y value that you want to move the node to
   * @return True if valid and successful, false otherwise.
   */
  // Noah
  public static boolean moveNode(String nodeID, int x, int y) throws DBException {
    try {
      String query = "UPDATE nodes SET xcoord = ?, ycoord = ? WHERE nodeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setInt(1, x);
      stmt.setInt(2, y);
      stmt.setString(3, nodeID);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: moveNode", e);
    }
  }

  /**
   * Deletes the node wiht the given nodeID from the database
   *
   * @param nodeID the nodeID of the node to be deleted
   * @return true if delete successful, false otherwise.
   */
  // Noah
  public static boolean deleteNode(String nodeID) throws DBException {
    try {
      String query = "DELETE FROM nodes WHERE (nodeID = ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      return stmt.executeUpdate() > 0;
      // return statement.getUpdateCount() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: deleteNode", e);
    }
  }

  /**
   * Gets the node with the specified nodeID
   *
   * @param nodeID nodeID of the node
   * @return the specified node
   */
  // Noah
  public static DbNode getNode(String nodeID) throws DBException {
    try {
      String query = "SELECT * FROM nodes WHERE nodeID = ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      ResultSet rs = stmt.executeQuery();
      DbNode sample = null;
      if (rs.next())
        sample =
            new DbNode(
                rs.getString("nodeID"),
                rs.getInt("xcoord"),
                rs.getInt("ycoord"),
                rs.getInt("floor"),
                rs.getString("building"),
                rs.getString("nodeType"),
                rs.getString("longName"),
                rs.getString("shortName"),
                rs.getString("teamAssigned").charAt(0));
      return sample;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getNode", e);
    }
  }

  /** Initializes the database, should be run before interfacing with it. */
  // doesn't need to use prepared statements since it takes no user input
  public static void initDB() throws ClassNotFoundException, SQLException, DBException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    String URL;

    URL = "jdbc:derby:memory:MapDB;create=true";
    con = DriverManager.getConnection(URL);
    statement = con.createStatement();
    String query;

    // not necessary when running the database in memory
    //    try {
    //      query = "DROP TABLE nodes";
    //      statement.execute(query);
    //    } catch (SQLException e) {
    //      if (!e.getSQLState().equals("42Y55")) throw e;
    //    }
    //
    //    try {
    //      query = "DROP TABLE edges";
    //      statement.execute(query);
    //    } catch (SQLException e) {
    //      if (!e.getSQLState().equals("42Y55")) throw e;
    //    }

    try {
      query =
          "CREATE TABLE nodes ("
              + "nodeID CHAR(10) NOT NULL PRIMARY KEY, "
              + "xcoord INT NOT NULL, "
              + "ycoord INT NOT NULL, "
              + "floor INT NOT NULL, "
              + "building VARCHAR(255) NOT NULL, "
              + "nodeType CHAR(4) NOT NULL CONSTRAINT TYPE_CK CHECK (nodeType IN ('HALL', 'ELEV', 'REST', 'STAI', 'DEPT', 'LABS', 'INFO', 'CONF', 'EXIT', 'RETL', 'SERV')), "
              + "longName VARCHAR(255) NOT NULL, "
              + "shortName VARCHAR(255) NOT NULL, "
              + "teamAssigned CHAR(1) NOT NULL"
              + ")";
      statement.execute(query);
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) throw e;
    }

    try {
      query =
          "CREATE TABLE edges ("
              + "edgeID CHAR(21) NOT NULL PRIMARY KEY, "
              + "node1 CHAR(10) NOT NULL, "
              + "node2 CHAR(10) NOT NULL, "
              + "FOREIGN KEY (node1) REFERENCES nodes(nodeID) ON DELETE CASCADE,"
              + "FOREIGN KEY (node2) REFERENCES nodes(nodeID) ON DELETE CASCADE"
              + ")";
      statement.execute(query);
    } catch (SQLException e) {
      if (!e.getSQLState().equals("X0Y32")) throw e;
    }

    DoctorController.initDoctor();
    EmployeeController.initEmployee();
  }

  /**
   * Gets the next available number for a particular node type for the purposes of making the nodeID
   *
   * @param nodeType the node type to search for
   * @return A formatted string with three digits and leading zeros for the next available number
   * @throws SQLException if something goes wrong with the sql
   */
  // Chris
  private static String nextAvailNum(String nodeType) throws SQLException {
    String query = "SELECT nodeID FROM nodes WHERE nodeType = ?";
    PreparedStatement stmt = con.prepareStatement(query);
    stmt.setString(1, nodeType);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Integer> nums = new ArrayList<Integer>();
    while (rs.next()) {
      try {
        nums.add(Integer.parseInt(rs.getString("nodeID").substring(5, 8)));
      } catch (NumberFormatException e) {
        continue; // skip all nodes with an invalid nodeID
      }
    }
    int size = nums.size();
    int val;
    int nextVal;
    int lowest;
    for (int i = 0; i < size; i++) {
      if (nums.get(i) <= 0 || nums.get(i) > size) continue;
      val = nums.get(i);
      while (nums.get(val - 1) != val) {
        nextVal = nums.get(val - 1);
        nums.set(val - 1, val);
        val = nextVal;
        if (val <= 0 || val > size) break;
      }
    }
    lowest = size + 1;
    for (int i = 0; i < size; i++) {
      if (nums.get(i) != i + 1) {
        lowest = i + 1;
        break;
      }
    }
    return String.format("%03d", lowest);
  }

  /**
   * Adds a node to the database, the NodeID is generated automatically and the teamAssigned is I
   * indicating a node added through the interface.
   *
   * @param x The x coordinate of the node
   * @param y The y coordinate of the node
   * @param floor The floor of the node
   * @param building The building the node is in
   * @param nodeType The node's type
   * @param longName The node's longName
   * @param shortName The node's shortName
   * @return True if valid and inserted properly, false otherwise.
   */
  // Chris
  public static DbNode addNode(
      int x, int y, int floor, String building, String nodeType, String longName, String shortName)
      throws DBException {
    try {
      String nodeID =
          "I" + nodeType.toUpperCase() + nextAvailNum(nodeType) + String.format("%02d", floor);
      String query =
          "INSERT INTO nodes VALUES ('"
              + nodeID
              + "', "
              + x
              + ","
              + y
              + ","
              + floor
              + ",'"
              + building
              + "','"
              + nodeType
              + "','"
              + longName.replace("'", "\\'")
              + "','"
              + shortName.replace("'", "\\'")
              + "','I')";
      statement.execute(query);
      return getNode(nodeID);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown Error: addNode", e);
    }
  }

  /**
   * Searches nodes by floor, building, nodeType and longName. All must be exact except longName,
   * which is a substring and case-insensitive.
   *
   * @param floor the floor you want the nodes on
   * @param building the building in which you want the nodes
   * @param nodeType the type of node you want (must be length 4)
   * @param longName the longname you want to search for
   * @return A linked list of DbNodes which matches the search query
   */
  // Nick
  public static LinkedList<DbNode> searchNode(
      int floor, String building, String nodeType, String longName) throws DBException {
    return searchNode(floor, building, nodeType, longName, false);
  }

  /**
   * Searches nodes by floor, building, nodeType and longName. All must be exact except longName,
   * which is a substring and case-insensitive. only returns visible nodes; basically, excludes hall
   * nodes.
   *
   * @param floor the floor you want the nodes on
   * @param building the building in which you want the nodes
   * @param nodeType the type of node you want (must be length 4)
   * @param longName the longname you want to search for
   * @return A linked list of DbNodes which matches the search query
   */
  public static LinkedList<DbNode> searchVisNode(
      int floor, String building, String nodeType, String longName) throws DBException {
    return searchNode(floor, building, nodeType, longName, true);
  }

  /**
   * Searches nodes by floor, building, nodeType, longName, and can exclude invisible (HALL) nodes.
   * All must be exact except longName, which is a substring and case-insensitive.
   *
   * @param floor the floor you want the nodes on
   * @param building the building in which you want the nodes
   * @param nodeType the type of node you want (must be length 4)
   * @param longName the longname you want to search for
   * @param visOnly True if you want to exclude hall nodes, false otherwise.
   * @return A linked list of DbNodes which matches the search query
   */
  // Nick
  private static LinkedList<DbNode> searchNode(
      int floor, String building, String nodeType, String longName, boolean visOnly)
      throws DBException {
    String query = "SELECT * FROM nodes WHERE ";
    if (visOnly) query = query + "(NOT nodeType = 'HALL') AND ";
    LinkedList<String> queries = new LinkedList<String>();
    if (floor >= 0) queries.add("floor = ? ");
    if (building != null) queries.add("building = ? ");
    if (nodeType != null) queries.add("nodeType = ? ");
    if (longName != null) queries.add("UPPER(longName) LIKE ? ");
    if (queries.size() == 0)
      throw new DBException("Error, searchNode: You must enter at least one search term!");
    Iterator<String> it = queries.iterator();
    while (true) {
      query = query + it.next();
      if (it.hasNext()) query = query + " AND ";
      else break;
    }

    try {
      PreparedStatement st = con.prepareStatement(query);
      int counter = 0;
      if (floor >= 0) st.setInt(++counter, floor);
      if (building != null) st.setString(++counter, building);
      if (nodeType != null) st.setString(++counter, nodeType);
      if (longName != null) st.setString(++counter, ("%" + longName.toUpperCase() + "%"));

      return getAllNodesSQL(st);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: searchNode", e);
    }
  }

  /**
   * Gets the graph-style node with the specified nodeID with a score of zero
   *
   * @param nodeID the nodeID of the node to fetch
   * @return the specified graph-style Node
   */
  // Chris
  public static Node getGNode(String nodeID) throws DBException {
    ResultSet rs;
    int x;
    int y;
    String id;

    try {
      rs =
          statement.executeQuery(
              "SELECT xcoord, ycoord, nodeID FROM nodes WHERE nodeID = '" + nodeID + "'");

      rs.next();

      x = rs.getInt("xcoord");
      y = rs.getInt("ycoord");
      id = rs.getString("nodeID");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getGNode", e);
    }

    return new Node(x, y, id);
  }

  /**
   * Gets the graph-style nodes of all nodes adjacent to the specified Node
   *
   * @param nodeID The ID of the specified node
   * @return A LinkedList containing all graph-style nodes adjacent to the specified node, or null
   *     if there was an issue retrieving the nodes
   */
  // Chris
  public static LinkedList<Node> getGAdjacent(String nodeID) throws DBException {
    LinkedList<Node> ret = new LinkedList<Node>();
    try {
      ResultSet rs = null;
      String query =
          "SELECT nodeID, xcoord, ycoord FROM (SELECT nodeID, xcoord, ycoord  FROM nodes) AS nodes, edges "
              + "WHERE ((edges.node1 = ? AND nodes.nodeID = edges.node2) OR (edges.node2 = ? AND nodes.nodeID = edges.node1))";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      stmt.setString(2, nodeID);
      // System.out.println(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        ret.add(new Node(rs.getInt("xcoord"), rs.getInt("ycoord"), rs.getString("nodeID")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getGAdjacent", e);
    }

    return ret;
  }

  public static LinkedList<Node> getGAdjacent(String nodeID, int startFloor, int endFloor)
      throws DBException {
    LinkedList<Node> ret = new LinkedList<Node>();
    try {
      ResultSet rs = null;
      String query =
          "SELECT nodeID, xcoord, ycoord FROM (SELECT nodeID, xcoord, ycoord FROM nodes WHERE"
              + " (nodes.floor = ? OR nodes.floor = ? OR nodes.nodeType = 'ELEV' OR nodes.nodeType = 'STAI')) AS nodes, edges "
              + "WHERE ((edges.node1 = ? AND nodes.nodeID = edges.node2) OR (edges.node2 = ? AND nodes.nodeID = edges.node1))";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(3, nodeID);
      stmt.setString(4, nodeID);
      stmt.setInt(1, startFloor);
      stmt.setInt(2, endFloor);
      // System.out.println(query);
      rs = stmt.executeQuery();
      while (rs.next()) {
        ret.add(new Node(rs.getInt("xcoord"), rs.getInt("ycoord"), rs.getString("nodeID")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getGAdjacent", e);
    }

    return ret;
  }

  /**
   * Gets a list of all the nodes on the specified floor
   *
   * @param floor the floor from which you want to get all the nodes
   * @param building the building which has the floor from which you want to get all the nodes
   * @return a LinkedList of all the nodes with the specified floor
   */
  public static LinkedList<DbNode> floorNodes(int floor, String building) throws DBException {
    String query = "SELECT * FROM nodes WHERE floor = ? AND building = ?";

    try {
      PreparedStatement st = con.prepareStatement(query);

      st.setInt(1, floor);
      st.setString(2, building);

      return getAllNodesSQL(st);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: floorNodes", e);
    }
  }

  /**
   * Gets a list of all the nodes on a floor except for invisible (HALL) nodes
   *
   * @param floor The floor from which to get the nodes
   * @param building The building from which to get the nodes
   * @return a LinkedList containing the nodes, or null if there was an issue with retrieving the
   *     nodes
   */
  // Nick
  public static LinkedList<DbNode> visNodes(int floor, String building) throws DBException {
    LinkedList<DbNode> ret = new LinkedList<DbNode>();

    String query = "SELECT * FROM nodes WHERE floor = ? AND building = ? AND NOT nodeType = 'HALL'";

    try {
      PreparedStatement st = con.prepareStatement(query);

      st.setInt(1, floor);
      st.setString(2, building);

      return getAllNodesSQL(st);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: visNodes", e);
    }
  }

  /**
   * Gets a list of all the nodes in the database.
   *
   * @return A LinkedList of all the nodes in the database
   */
  public static LinkedList<DbNode> allNodes() throws DBException {
    LinkedList<DbNode> nodes = new LinkedList<DbNode>();

    String query = "SELECT * FROM nodes";

    try {
      return getAllNodesSQL(con.prepareStatement(query));
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: allNodes", e);
    }
  }

  /**
   * Gets all nodes that match a particular sql query returns them as a linked list
   *
   * @param st the PreparedStatement to select nodes with
   * @return a LinkedList of all the DbNodes which match the sql query
   */
  // Nick
  private static LinkedList<DbNode> getAllNodesSQL(PreparedStatement st) throws SQLException {
    LinkedList<DbNode> nodes = new LinkedList<DbNode>();

    ResultSet rs = st.executeQuery();
    while (rs.next()) {
      nodes.add(
          new DbNode(
              rs.getString("nodeID"),
              rs.getInt("xcoord"),
              rs.getInt("ycoord"),
              rs.getInt("floor"),
              rs.getString("building"),
              rs.getString("nodeType"),
              rs.getString("longName"),
              rs.getString("shortName"),
              rs.getString("teamAssigned").charAt(0)));
    }
    return nodes;
  }

  /**
   * Gets a list of all the nodes with an edge to the specified node
   *
   * @param nodeID The nodeID of the node for which you want the edges
   * @return All the nodes directly connected to the passed-in one
   */
  // Nick
  public static LinkedList<DbNode> getAdjacent(String nodeID) throws DBException {
    LinkedList<DbNode> ret = new LinkedList<DbNode>();
    try {

      ResultSet rs = null;
      String query =
          "SELECT nodes.* FROM nodes, edges WHERE (edges.node1 = ? AND nodes.nodeID = edges.node2) OR (edges.node2 = ? AND nodes.nodeID = edges.node1)";

      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, nodeID);
      st.setString(2, nodeID);
      rs = st.executeQuery();

      while (rs.next()) {
        ret.add(
            new DbNode(
                rs.getString("nodeID"),
                rs.getInt("xcoord"),
                rs.getInt("ycoord"),
                rs.getInt("floor"),
                rs.getString("building"),
                rs.getString("nodeType"),
                rs.getString("longName"),
                rs.getString("shortName"),
                rs.getString("teamAssigned").charAt(0)));
      }
      //      query = "DROP VIEW connected_edges";
      //      statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: getAdjacent", e);
    }

    return ret;
  }

  /**
   * Adds an edge to the graph
   *
   * @param nodeID1 the nodeID of the first edge
   * @param nodeID2 the nodeID of the second edge
   * @return True if valid and successful, false otherwise
   */
  // Nick
  public static boolean addEdge(String nodeID1, String nodeID2) throws DBException {
    String edgeID = nodeID1 + "_" + nodeID2;
    try {
      String query = "SELECT floor, nodeType FROM nodes WHERE (nodeID = ?) OR (nodeID = ?)";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, nodeID1);
      st.setString(2, nodeID2);
      ResultSet result = st.executeQuery();
      result.next();
      String floor1 = result.getString("floor");
      String type1 = result.getString("nodeType");
      result.next();
      String floor2 = result.getString("floor");
      String type2 = result.getString("nodeType");

      if (!floor1.equals(floor2)) {
        if (!type1.equals(type2) || !(type1.equals("STAI") || type1.equals("ELEV"))) {
          throw new DBException("Cannot add edge between " + nodeID1 + " and " + nodeID2);
        }
      }

      // Look in to a more efficient way to do this, but it's probably OK for now
      query = "SELECT * FROM edges WHERE (node1 = ? AND node2 = ?) OR (node2 = ? AND node1 = ?)";

      st = con.prepareStatement(query);
      st.setString(1, nodeID1);
      st.setString(2, nodeID2);
      st.setString(3, nodeID1);
      st.setString(4, nodeID2);
      result = st.executeQuery();

      if (result.next()) {
        return false;
      }

      query = "INSERT INTO edges VALUES (?, ?, ?)";

      st = con.prepareStatement(query);
      st.setString(1, edgeID);
      st.setString(2, nodeID1);
      st.setString(3, nodeID2);

      return st.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addEdge", e);
    }
  }

  /**
   * Removes an edge from the graph
   *
   * @param nodeID1 the nodeID of the first node
   * @param nodeID2 the nodeID of the second node
   * @return True if valid and successful, false otherwise
   */
  // Nick
  public static boolean removeEdge(String nodeID1, String nodeID2) throws DBException {
    //    String query = "SELECT * FROM edges WHERE edgeID = '" + edgeID + "'";
    //    ResultSet result = statement.executeQuery(query);
    //
    //    if(!result.next()){
    //      return false;
    //    }
    // top method probably works, but is inefficient
    try {
      String query =
          "DELETE FROM edges WHERE (node1 = ? AND node2 = ?) OR (node2 = ? AND node1 = ?)";
      PreparedStatement st = con.prepareStatement(query);
      st.setString(1, nodeID1);
      st.setString(2, nodeID2);
      st.setString(3, nodeID1);
      st.setString(4, nodeID2);
      return st.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: removeEdge", e);
    }
  }

  /**
   * Exports all the edges for CSV purposes
   *
   * @return a linked list of each edge in CSV format
   */
  public static LinkedList<String> exportEdges() throws DBException {
    try {
      LinkedList<String> edges = new LinkedList<String>();
      String query = "SELECT * FROM edges";
      ResultSet rs = con.prepareStatement(query).executeQuery();
      while (rs.next()) {
        edges.add(
            rs.getString("edgeID") + "," + rs.getString("node1") + "," + rs.getString("node2"));
      }
      return edges;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: exportEdges", e);
    }
  }

  /** Clears all of the nodes and edges from the database */
  public static void clearNodes() throws DBException {
    try {
      String query = "DELETE FROM nodes";
      statement.executeUpdate(query);
      query = "DELETE FROM edges";
      statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: clearNodes", e);
    }
  }
}
