package edu.wpi.N.database;

import edu.wpi.N.Main;
import edu.wpi.N.entities.DbNode;
import edu.wpi.N.views.features.ArduinoController;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.ibatis.jdbc.ScriptRunner;

public class MapDB {

  private static Statement statement;
  private static Connection con;

  public static Connection getCon() {
    return con;
  }

  /** Initializes the database, should be run before interfacing with it. */
  // doesn't need to use prepared statements since it takes no user input
  public static void initDB() throws ClassNotFoundException, SQLException, DBException {
    if (con == null || statement == null) {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
      String URL;
      URL = "jdbc:derby:MapDB";
      try {
        con = DriverManager.getConnection(URL);
      } catch (SQLException e) {
        if (e.getSQLState().equals("XJ004")) { // db doesn't exist, create it
          URL = "jdbc:derby:MapDB;create=true";
          con = DriverManager.getConnection(URL);
          setupDB.main(new String[] {});
        } else throw e;
      }
      statement = con.createStatement();
    }

    addHardCodedLogins();
  }

  //  /** Initializes a database in memory for tests */
  //  public static void initTestDB()
  //      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
  //    if (con == null || statement == null) {
  //      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
  //      String URL;
  //      URL = "jdbc:derby:memory:db;create=true";
  //      con = DriverManager.getConnection(URL);
  //      statement = con.createStatement();
  //      ScriptRunner sr = new ScriptRunner(con);
  //      Reader reader =
  //          new BufferedReader(
  //              new InputStreamReader(Main.class.getResourceAsStream("sql/setup.sql")));
  //      sr.runScript(reader);
  //    }
  //  }

  /**
   * Same as initTestDB except the database is guarenteed to be completely empty after running this
   * function. Use whenever possible.
   *
   * @throws SQLException on error
   * @throws ClassNotFoundException on error
   */
  public static void initTestDB() throws SQLException, ClassNotFoundException {
    if (con != null) {
      ScriptRunner sr = new ScriptRunner(con);
      Reader reader =
          new BufferedReader(
              new InputStreamReader(Main.class.getResourceAsStream("sql/drop.sql"))); // drop tables
      sr.runScript(reader);
    } else {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
      String URL;
      URL = "jdbc:derby:memory:db;create=true";
      con = DriverManager.getConnection(URL);
      statement = con.createStatement();
    }
    ScriptRunner sr = new ScriptRunner(con);
    Reader reader =
        new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("sql/setup.sql")));
    sr.runScript(reader);

    addHardCodedLogins();
  }

  private static void addHardCodedLogins() throws SQLException {
    BCryptSingleton hasher = BCryptSingleton.getInstance();
    PreparedStatement st =
        con.prepareStatement("INSERT INTO credential VALUES ('Gaben', ?, 'ADMIN')");
    st.setBytes(1, hasher.hash("MoolyFTW"));
    try {
      st.executeUpdate();
    } catch (SQLException e) {
      if (!e.getSQLState()
          .equals("23505")) { // primary key/unique constraint violation - login already exists
        throw e;
      }
    }

    st = con.prepareStatement("INSERT INTO credential VALUES ('admin', ?, 'ADMIN')");
    st.setBytes(1, hasher.hash("admin"));
    try {
      st.executeUpdate();
    } catch (SQLException e) {
      if (!e.getSQLState()
          .equals("23505")) { // primary key/unique constraint violation - login already exists
        throw e;
      }
    }
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
      // stmt.setString(7, longName.replace("\'", "\\'"));
      // stmt.setString(8, shortName.replace("\'", "\\'"));
      stmt.setString(7, longName);
      stmt.setString(8, shortName);
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
   * Modifies a node unsafely. Probably shouldn't ever use. Can mess with nodeID
   *
   * @param nodeID the nodeID of the node you want to modify
   * @param x the new x value
   * @param y the new y value
   * @param floor the new floor
   * @param building the new building
   * @param nodeType the new nodeType
   * @param longName the new longName
   * @param shortName the new shortName
   * @param teamAssigned the new teamAssigned
   * @return True if the node is modified successfully, false otherwise
   * @throws DBException On error
   */
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
    LinkedList<DbNode> edges = new LinkedList<>();
    String query;
    try {
      con.setAutoCommit(false);
      if (!(nodeID.substring(0, 5) + nodeID.substring(8))
          .equals(teamAssigned + nodeType.toUpperCase() + String.format("%02d", floor))) {
        if (nodeID.substring(1, 5).equals(nodeType)) {
          newID = teamAssigned + nodeID.substring(1, 8) + String.format("%02d", floor);
        } else {
          newID =
              teamAssigned + nodeType.toUpperCase() + nextAvailNum(nodeType, floor) + "0" + floor;
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
      stmt.setString(7, longName.replace("'", "\\'"));
      stmt.setString(8, shortName.replace("'", "\\'"));
      stmt.setString(9, String.valueOf(teamAssigned));
      stmt.setString(10, nodeID);
      stmt.executeUpdate();
      for (DbNode edge : edges) {
        addEdge(newID, edge.getNodeID());
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
   * Deletes the node with the given nodeID from the database
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

  /**
   * Gets the next available number for a particular node type for the purposes of making the nodeID
   *
   * @param nodeType the node type to search for
   * @return A formatted string with three digits and leading zeros for the next available number
   * @throws SQLException if something goes wrong with the sql
   */
  // Chris
  private static String nextAvailNum(String nodeType, int floor) throws SQLException {
    String query = "SELECT nodeID FROM nodes WHERE nodeType = ? AND floor = ?";
    PreparedStatement stmt = con.prepareStatement(query);
    stmt.setString(1, nodeType);
    stmt.setInt(2, floor);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Integer> nums = new ArrayList<>();
    while (rs.next()) {
      try {
        nums.add(Integer.parseInt(rs.getString("nodeID").substring(5, 8)));
      } catch (NumberFormatException e) {
        // skip all nodes with an invalid nodeID
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
   * @return a DBNode containing the information of the node that was just created.
   */
  // Chris
  public static DbNode addNode(
      int x, int y, int floor, String building, String nodeType, String longName, String shortName)
      throws DBException {
    try {
      String nodeID =
          "I"
              + nodeType.toUpperCase()
              + nextAvailNum(nodeType, floor)
              + String.format("%02d", floor);
      String query = "INSERT INTO nodes VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      stmt.setInt(2, x);
      stmt.setInt(3, y);
      stmt.setInt(4, floor);
      stmt.setString(5, building);
      stmt.setString(6, nodeType);
      // stmt.setString(7, longName.replace("\'", "\\'"));
      // stmt.setString(8, shortName.replace("\'", "\\'"));
      stmt.setString(7, longName);
      stmt.setString(8, shortName);
      stmt.setString(9, "I");
      stmt.executeUpdate();
      // System.out.println("Values Inserted");
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
    LinkedList<String> queries = new LinkedList<>();
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

  //  /**
  //   * Gets the graph-style node with the specified nodeID with a score of zero
  //   *
  //   * @param nodeID the nodeID of the node to fetch
  //   * @return the specified graph-style Node
  //   */
  //  // Chris
  //  public static Node getGNode(String nodeID) throws DBException {
  //    ResultSet rs;
  //    int x;
  //    int y;
  //    String id;
  //
  //    try {
  //      rs =
  //          statement.executeQuery(
  //              "SELECT xcoord, ycoord, nodeID FROM nodes WHERE nodeID = '" + nodeID + "'");
  //
  //      rs.next();
  //
  //      x = rs.getInt("xcoord");
  //      y = rs.getInt("ycoord");
  //      id = rs.getString("nodeID");
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //      throw new DBException("Unknown error: getGNode", e);
  //    }
  //
  //    return new Node(x, y, id);
  //  }

  //  /**
  //   * Gets the graph-style nodes of all nodes adjacent to the specified Node
  //   *
  //   * @param nodeID The ID of the specified node
  //   * @return A LinkedList containing all graph-style nodes adjacent to the specified node, or
  // null
  //   *     if there was an issue retrieving the nodes
  //   */
  //  // Chris
  //  public static LinkedList<Node> getGAdjacent(String nodeID) throws DBException {
  //    LinkedList<Node> ret = new LinkedList<Node>();
  //    try {
  //      ResultSet rs = null;
  //      String query =
  //          "SELECT nodeID, xcoord, ycoord FROM (SELECT nodeID, xcoord, ycoord  FROM nodes) AS
  // nodes, edges "
  //              + "WHERE ((edges.node1 = ? AND nodes.nodeID = edges.node2) OR (edges.node2 = ? AND
  // nodes.nodeID = edges.node1))";
  //      PreparedStatement stmt = con.prepareStatement(query);
  //      stmt.setString(1, nodeID);
  //      stmt.setString(2, nodeID);
  //      // System.out.println(query);
  //      rs = stmt.executeQuery();
  //      while (rs.next()) {
  //        ret.add(new Node(rs.getInt("xcoord"), rs.getInt("ycoord"), rs.getString("nodeID")));
  //      }
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //      throw new DBException("Unknown error: getGAdjacent", e);
  //    }
  //
  //    return ret;
  //  }

  /**
   * Returns the Graph-style nodes adjacent to the given node and on either of the floors passed in,
   * along with stairs/elevators
   *
   * @param nodeID the ID of the node you need the adjacents for
   * @param startFloor the starting floor on the path
   * @param endFloor the end floor on the path
   * @return A linked list of all the adjacent nodes on the proper floors or of the proper node type
   * @throws DBException on error
   */
  public static LinkedList<DbNode> getAdjacent(String nodeID, int startFloor, int endFloor)
      throws DBException {
    return getAdjacent(nodeID, startFloor, endFloor, false);
  }

  /**
   * Returns the Graph-style nodes adjacent to the given node and on either of the floors passed in,
   * along with elevators. Can exclude stair nodes.
   *
   * @param nodeID the ID of the node you need the adjacents for
   * @param startFloor the starting floor on the path
   * @param endFloor the end floor on the path
   * @param wheelAccess true if you want to exclude STAI nodes, false otherwise.
   * @return A linked list of all the adjacent nodes on the proper floors or of the proper node type
   * @throws DBException on error
   */
  public static LinkedList<DbNode> getAdjacent(
      String nodeID, int startFloor, int endFloor, boolean wheelAccess) throws DBException {
    LinkedList<DbNode> ret = new LinkedList<DbNode>();
    try {
      ResultSet rs = null;
      String query;
      if (wheelAccess) {
        query =
            "SELECT nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned FROM (SELECT * FROM nodes WHERE"
                + " ((nodes.floor = ? OR nodes.floor = ? OR nodes.nodeType = 'ELEV') AND NOT nodes.nodeType = 'STAI')) AS nodes,"
                + " (SELECT node1, node2 FROM edges  WHERE (edges.node1 = ?) OR (edges.node2 =  ?)) AS edges "
                + "WHERE edges.node1 = nodes.nodeID OR edges.node2 = nodes.nodeID";
      } else {
        query =
            "SELECT nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName, teamAssigned FROM (SELECT * FROM nodes WHERE"
                + " (nodes.floor = ? OR nodes.floor = ? OR nodes.nodeType = 'ELEV' OR  nodes.nodeType = 'STAI')) AS nodes,"
                + " (SELECT node1, node2 FROM edges  WHERE (edges.node1 = ?) OR (edges.node2 = ?)) AS edges "
                + "WHERE edges.node1 = nodes.nodeID OR edges.node2 = nodes.nodeID";
      }
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(3, nodeID);
      stmt.setString(4, nodeID);
      stmt.setInt(1, startFloor);
      stmt.setInt(2, endFloor);
      // System.out.println(query);
      rs = stmt.executeQuery();
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
    LinkedList<DbNode> nodes = new LinkedList<>();

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
    LinkedList<DbNode> ret = new LinkedList<>();
    try {

      ResultSet rs;
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
      int floor1 = result.getInt("floor");
      String type1 = result.getString("nodeType");
      result.next();
      int floor2 = result.getInt("floor");
      String type2 = result.getString("nodeType");

      if (floor1 != floor2) {
        if (!type1.equals(type2) || !(type1.equals("STAI") || type1.equals("ELEV"))) {
          throw new DBException(
              "Cannot add edge between "
                  + nodeID1
                  + " and "
                  + nodeID2
                  + "since they are on different floors and not stairs or elevators");
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
      //noinspection JpaQueryApiInspection
      st.setString(3, nodeID2);

      boolean updated = st.executeUpdate() > 0;
      if (floor1 != floor2) { // want to do this after the edge is added
        addToShaft(nodeID1, nodeID2);
      }
      return updated;
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
   * Adds two nodes to the same shaft. If neither of them are in a shaft, makes a new shaft with
   * them. If one of them is in a shaft, then adds the other one to that shaft. If both of them are
   * in different shafts, then the shafts get merged
   *
   * @param node1 the nodeID of the first node of the edge that should be added to the shaft
   * @param node2 the nodeID of the second node of the edge that should be added to the shaft
   * @throws DBException when both are in different shafts or on error
   */
  public static void addToShaft(String node1, String node2) throws DBException {
    if (node1.equals(node2)) throw new DBException("Both of those nodes are the same!");
    DbNode DbNode1 = getNode(node1);
    DbNode DbNode2 = getNode(node2);
    if (DbNode1 == null || DbNode2 == null)
      throw new DBException("One of those nodes doesn't exist!");
    String error = "";
    if (DbNode1.getFloor() == DbNode2.getFloor())
      error += "You can't add two nodes on the same floor to a shaft!\n";
    if (!DbNode1.getNodeType().equals(DbNode2.getNodeType()))
      error += "Nodes in a shaft must be the same node type!\n";
    if (!DbNode1.getBuilding().equals(DbNode2.getBuilding()))
      error += "Nodes in a shaft must be in the same building!\n";
    if (!(DbNode1.getNodeType().equals("ELEV") || DbNode1.getNodeType().equals("STAI")))
      error += "Nodes in a shaft must be either elevators or stairs!\n";
    // makes sure that no nodes on the same floor are ever in the same shaft
    try {
      for (DbNode next : getInShaft(node1)) {
        if (next.getFloor() == DbNode2.getFloor()) {
          if (next.getNodeID().equals(DbNode2.getNodeID()))
            return; // already in the same shaft, just return.
          error +=
              DbNode2.getLongName()
                  + " is on the same floor as another node in "
                  + DbNode1.getLongName()
                  + "'s shaft!\n";
          break;
        }
      }
    } catch (DBException ignored) {
    } // do nothing, just means that node1 isn't in any shafts
    try {
      for (DbNode next : getInShaft(node2)) {
        if (next.getFloor() == DbNode1.getFloor()) {
          if (next.getNodeID().equals(DbNode1.getNodeID()))
            return; // already in the same shaft, just return.
          error +=
              DbNode1.getLongName()
                  + " is on the same floor as another node in "
                  + DbNode2.getLongName()
                  + "'s shaft!\n";
          break;
        }
      }
    } catch (DBException ignored) {
    }
    if (error.length() != 0) throw new DBException(error);
    // done enforcing most constraints: The actual code follows
    String query = "SELECT shaftID, nodeID FROM shaft WHERE nodeID = ? OR nodeID = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, node1);
      stmt.setString(2, node2);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) { // one or more in shaft
        String nodeID1 = rs.getString("nodeID");
        int shaftID1 = rs.getInt("shaftID");
        if (rs.next()) { // both in shaft, check if both have same id
          int shaftID2 = rs.getInt("shaftID");
          if (shaftID1 == shaftID2) return; // both already in the same shaft, do nothing
          else { // nodes are in different shafts and the shafts must be merged
            query =
                "UPDATE shaft SET shaftID = ? WHERE shaftID = ?"; // simply sets the shaftIDs for
            // each shaft equal
            // nodeID2 to the shaft of shaftID1
            stmt = con.prepareStatement(query);
            stmt.setInt(1, shaftID1);
            stmt.setInt(2, shaftID2);
            stmt.executeUpdate();
          }

        } else { // only one in shaft
          String nodeID2 = node2;
          if (nodeID1.equals(node2))
            nodeID2 = node1; // nodeID2 is the one not found in the result set first
          query = "INSERT INTO shaft (shaftID, nodeID) VALUES (?, ?)";
          stmt = con.prepareStatement(query);
          stmt.setInt(1, shaftID1);
          stmt.setString(2, nodeID2);
          stmt.executeUpdate();
        }
      } else { // neither in shaft, make a new one and insert both
        query = "INSERT INTO shaft (nodeID) VALUES (?)";
        stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, node1);
        stmt.executeUpdate();
        rs = stmt.getGeneratedKeys();
        rs.next();
        int shaftID1 = rs.getInt(1);
        query = "INSERT INTO shaft (shaftID, nodeID) VALUES (?, ?)";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, shaftID1);
        stmt.setString(2, node2);
        stmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: addToShaft", e);
    }
  }

  /**
   * Removes the given nodeID from any shaft it might be in
   *
   * @param nodeID the nodeID to remove from a shaft
   * @throws DBException When that nodeID isn't in a shaft or on error
   */
  public static void removeFromShaft(String nodeID) throws DBException {
    String query = "DELETE FROM shaft WHERE NODEID = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      if (stmt.executeUpdate() <= 0) throw new DBException("That node isn't in a shaft!");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: removeFromShaft " + nodeID, e);
    }
  }

  /**
   * gets all the DbNodes in the same shaft as the given nodeID, including the node represented by
   * the given ID
   *
   * @param nodeID The ID of the node you want all the nodes in the shaft for
   * @return a linked list of DbNodes in the same shaft as the given nodeID
   * @throws DBException when the given node isn't in a shaft or on error
   */
  public static LinkedList<DbNode> getInShaft(String nodeID) throws DBException {
    String query = "SELECT shaftID FROM shaft WHERE nodeID = ?";
    LinkedList<DbNode> nodes = new LinkedList<>();
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      int shaftID = rs.getInt("shaftID");
      query = "SELECT nodeID FROM shaft WHERE shaftID = ?";
      stmt = con.prepareStatement(query);
      stmt.setInt(1, shaftID);
      rs = stmt.executeQuery();
      while (rs.next()) {
        nodes.add(getNode(rs.getString("nodeID")));
      }
      return nodes;
    } catch (SQLException e) {
      if (e.getSQLState().equals("24000")) { // invalid cursor state, no current row
        throw new DBException("That node isn't in any shafts!");
      } else {
        e.printStackTrace();
        throw new DBException("Unknown error: getInShaft " + nodeID, e);
      }
    }
  }

  /**
   * Returns a linked list of Linked Lists of DBNodes. Each linked list returned is all of the nodes
   * in a shaft.
   *
   * @param building The building that you want to get all of the shafts for
   * @return All of the shafts in a building
   * @throws DBException On error
   */
  public static LinkedList<LinkedList<DbNode>> getShafts(String building) throws DBException {
    String query =
        "SELECT shaftID, shaft.nodeID from shaft, "
            + "(SELECT nodeID FROM nodes WHERE (nodeType = 'ELEV' OR nodeTYPE = 'STAI') AND building = ?) as nodes"
            + " WHERE shaft.nodeID = nodes.nodeID order by shaftID"; // filter out nodes not in the
    // right building in this
    // statement rather than later
    LinkedList<LinkedList<DbNode>> shafts = new LinkedList<>();
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, building);
      ResultSet rs = stmt.executeQuery();
      boolean left = rs.next();
      while (left) {
        int shaft = rs.getInt("shaftID");
        LinkedList<DbNode> nodesInShaft = new LinkedList<>();
        do { // key to this is the order by statement, I know that all the nodes in a shaft will be
          // consecutive
          nodesInShaft.add(getNode(rs.getString("nodeID")));
        } while (((left = rs.next()) && rs.getInt("shaftID") == shaft));
        shafts.add(nodesInShaft);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown errors: getShafts ", e);
    }
    return shafts;
  }

  public static LinkedList<DbNode[]> getFloorEdges(int floor, String building) throws DBException {
    try {
      LinkedList<DbNode[]> ret = new LinkedList<>();
      String query =
          "SELECT edges.node1, n1.xcoord AS x1, n1.ycoord AS y1, n1.floor AS floor1, n1.building AS build1, n1.nodeType AS type1, "
              + "n1.longName AS long1, n1.shortName AS short1, n1.teamAssigned AS team1, "
              + "edges.node2, n2.xcoord AS x2, n2.ycoord AS y2, n2.floor AS floor2, n2.building AS build2, n2.nodeType AS type2, "
              + "n2.longName AS long2, n2.shortName AS short2, n2.teamAssigned AS team2 "
              + "FROM edges "
              + "JOIN nodes n1 ON edges.node1 = n1.nodeID "
              + "JOIN nodes n2 ON edges.node2 = n2.nodeID "
              + "WHERE n1.floor = ? AND n2.floor = ? AND n1.building = ? AND n2.building = ?";

      PreparedStatement st = con.prepareStatement(query);
      st.setInt(1, floor);
      st.setInt(2, floor);
      st.setString(3, building);
      st.setString(4, building);
      ResultSet rs = st.executeQuery();

      while (rs.next()) {
        DbNode node1 =
            new DbNode(
                rs.getString("node1"),
                rs.getInt("x1"),
                rs.getInt("y1"),
                rs.getInt("floor1"),
                rs.getString("build1"),
                rs.getString("type1"),
                rs.getString("long1"),
                rs.getString("short1"),
                rs.getString("team1").charAt(0));
        DbNode node2 =
            new DbNode(
                rs.getString("node2"),
                rs.getInt("x2"),
                rs.getInt("y2"),
                rs.getInt("floor2"),
                rs.getString("build2"),
                rs.getString("type2"),
                rs.getString("long2"),
                rs.getString("short2"),
                rs.getString("team2").charAt(0));

        ret.add(new DbNode[] {node1, node2});
      }

      return ret;

    } catch (SQLException e) {
      throw new DBException("Unknown error: getFloorEdges", e);
    }
  }

  /**
   * Exports all the edges for CSV purposes
   *
   * @return a linked list of each edge in CSV format
   */
  public static LinkedList<String> exportEdges() throws DBException {
    try {
      LinkedList<String> edges = new LinkedList<>();
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
      // unneccessary to explicitly delete from edges due to on delete cascade in foreign keys
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: clearNodes", e);
    }
  }

  /** Clears all of the edges from the database */
  public static void clearEdges() throws DBException {
    try {
      String query = "DELETE FROM edges";
      statement.executeUpdate(query);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: clearNodes", e);
    }
  }

  /**
   * Gets the node currently set up as the kiosk
   *
   * @return a DBNode representing the current Kiosk
   * @throws DBException When the kiosk hasn't been setup or on error.
   */
  public static DbNode getKiosk() throws DBException {
    try {
      String query = "SELECT nodeID from kiosk";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      return getNode(rs.getString("nodeID"));
    } catch (SQLException e) {
      if (e.getSQLState().equals("24000")) {
        throw new DBException("The kiosk has not been set up!");
      }
      e.printStackTrace();
      throw new DBException("Error: getKiosk is not working properly", e);
    }
  }

  public static int getKioskAngle() throws DBException {
    try {
      String query = "SELECT angle from kiosk";
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      return rs.getInt("angle");
    } catch (SQLException e) {
      if (e.getSQLState().equals("24000")) {
        throw new DBException("The kiosk has not been set up!");
      }
      e.printStackTrace();
      throw new DBException("Error: getKiosk is not working properly", e);
    }
  }

  /**
   * Sets the kiosk to the given nodeID and angle, which is transformed to be between 0 and 360
   * degrees. The old Kiosk tuple is deleted using triggers.
   *
   * @param nodeID the nodeID of the node you wish to be the Kiosk
   * @param angle The angle of the kiosk
   * @throws DBException On error or when the kiosk node doesn't exist
   */
  public static void setKiosk(String nodeID, int angle) throws DBException {
    angle -= 360 * (angle / 360);
    if (angle < 0) angle += 360;
    ArduinoController.setUpArrow(angle);
    String query =
        "INSERT INTO kiosk VALUES (?, ?)"; // The kiosk is kept as only one tuple using triggers
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, nodeID);
      stmt.setInt(2, angle);
      stmt.executeUpdate();
    } catch (SQLException e) {
      if (e.getSQLState().equals("23503")) {
        throw new DBException("That node doesn't exist and can not be the kiosk!");
      } else if (e.getSQLState().equals("23505")) { // node is already kiosk
        try {
          query = "UPDATE kiosk SET angle = ? WHERE nodeID = ?";
          PreparedStatement stmt = con.prepareStatement(query);
          stmt.setString(2, nodeID);
          stmt.setInt(1, angle);
          stmt.executeUpdate();
        } catch (SQLException ex) {
          throw new DBException("Unknown error: setKiosk", e);
        }
      } else {
        e.printStackTrace();
        throw new DBException("Unknown error: setKiosk: " + nodeID + " " + angle, e);
      }
    }
  }

  /**
   * Loads all Map edges and Nodes into a Hashmap
   *
   * @return Hashmap <NodeID, list of DbNodes has edges to>
   */
  public static HashMap<String, LinkedList<DbNode>> loadMapData() throws DBException {
    String query = "SELECT node1, node2 FROM edges";
    HashMap<String, LinkedList<DbNode>> map = new HashMap<>();
    try {
      PreparedStatement stmt = con.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String node1 = rs.getString("node1");
        String node2 = rs.getString("node2");
        if (map.get(node1) == null) map.put(node1, new LinkedList<DbNode>());
        if (map.get(node2) == null) map.put(node2, new LinkedList<DbNode>());
        map.get(node1).add(getNode(node2));
        map.get(node2).add(getNode(node1));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new DBException("Unknown error: loadMapData", e);
    }
    //    // Implementation using existing methods
    //    HashMap<String, LinkedList<DbNode>> result = new HashMap<String, LinkedList<DbNode>>();
    //    for (DbNode node : allNodes()) {
    //      String id = node.getNodeID();
    //      LinkedList<DbNode> adjacent = getAdjacent(id);
    //      result.put(id, adjacent);
    //    }
    //    return result;
    return map;
  }
}
