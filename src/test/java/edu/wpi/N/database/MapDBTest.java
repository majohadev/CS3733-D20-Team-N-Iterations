package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MapDBTest {
  @BeforeAll
  public static void setup()
      throws SQLException, ClassNotFoundException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    MapDB.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
    MapDB.addNode("NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');
    MapDB.addNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N');
    MapDB.addNode(
        "NDEPT01005", 1300, 1200, 5, "Faulkner", "DEPT", "Software Engineering", "Dept 10", 'N');
  }

  @Test
  public void testKiosk() throws DBException {
    MapDB.setKiosk("NDEPT00204", 40);
    MapDB.setKiosk("NHALL00104", -90);
    assertEquals(MapDB.getKiosk(), MapDB.getNode("NHALL00104"));
    assertEquals(MapDB.getKioskAngle(), 270);
    MapDB.setKiosk("NHALL00104", 390);
    assertEquals(MapDB.getKioskAngle(), 30);
    assertThrows(DBException.class, () -> MapDB.setKiosk("INVALID", 10));
  }
  // Noah
  @Test
  public void testAddNodeID() throws DBException {
    assertTrue(
        MapDB.addNode("NHALL01404", 771, 123, 4, "Faulkner", "HALL", "HALL 14", "Hall 14", 'N'));
    MapDB.deleteNode("NHALL01404");
  }

  // Noah
  @Test
  public void testModifyNode() throws DBException {
    MapDB.addEdge("NHALL00204", "NHALL00104");
    MapDB.modifyNode("NHALL00204", 123, 771, 4, "Faulkner", "CONF", "DEPT 3", "Dept 3", 'N');
    // DbTester.printDB();
    DbNode n = MapDB.getNode("NCONF00104");
    assertEquals(123, n.getX());
    assertEquals("CONF", n.getNodeType());
    MapDB.deleteNode("NCONF00104");
    MapDB.addNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N');
  }

  @Test
  public void testSafeModifyNode() throws DBException {
    MapDB.modifyNode("NDEPT00104", 1350, 420, "Cardio", "Dept X");
    DbNode n = MapDB.getNode("NDEPT00104");
    assertEquals(1350, n.getX());
    assertEquals(420, n.getY());
    assertEquals("Faulkner", n.getBuilding());
    assertEquals("Cardio", n.getLongName());
    assertEquals("Dept X", n.getShortName());
    MapDB.deleteNode("NDEPT00104");
    MapDB.addNode("NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
  }

  // Noah
  @Test
  public void testMoveNode() throws DBException {
    MapDB.moveNode("NHALL00204", 135, 445);
    DbNode n = MapDB.getNode("NHALL00204");
    assertTrue(n.getX() == 135 && n.getY() == 445);
    MapDB.moveNode("NHALL00204", 1350, 1250);
  }

  // Noah
  @Test
  public void testDeleteNode() throws DBException {
    MapDB.addNode("NHALL00704", 1250, 850, 4, "Faulkner", "HALL", "Hall 7", "Hall 7", 'N');
    MapDB.deleteNode("NHALL00704");
    assertNull(MapDB.getNode("NHALL00704"));
  }

  // Noah
  @Test
  public void testGetNode() throws DBException {
    DbNode n = MapDB.getNode("NDEPT00204");
    assertTrue(n.getX() == 1450 && n.getY() == 950);
  }

  // Chris
  @Test
  // TODO: just see if it contains
  public void testAddNodeNoID() throws DBException {
    /*MapDB.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7");
    assertTrue(MapDB.floorNodes(4, "Faulkner").contains());*/
    assertNotNull(MapDB.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7"));
    DbNode node = MapDB.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7");
    assertTrue(node.getNodeID() != null);
    assertTrue(MapDB.allNodes().get(MapDB.allNodes().size() - 1).getLongName().equals("Database"));
  }

  // Chris
  @Test
  public void testSearchNode() throws DBException {
    // need to search by floor, building, nodeType, longName.
    MapDB.addNode("NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N');
    MapDB.addNode("NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    MapDB.addNode("NELEV00X05", 1250, 850, 5, "Not Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    MapDB.addNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N');

    LinkedList<DbNode> lst = MapDB.searchNode(7, "Faulkner", "ELEV", "Elev X");
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N')));
    lst = MapDB.searchNode(-1, null, "ELEV", null);
    assertTrue(
        lst.contains(
            new DbNode(
                "NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertTrue(
        lst.contains(
            new DbNode("NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N')));
    lst = MapDB.searchNode(5, null, null, "ElEv");
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertTrue(
        lst.contains(
            new DbNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N')));
    assertTrue(
        lst.contains(
            new DbNode(
                "NELEV00X05", 1250, 850, 5, "Not Faulkner", "ELEV", "Elev X", "Hall 7", 'N')));
    lst = MapDB.searchVisNode(5, null, null, "ElEv");
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode(
                "NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N')));
    assertFalse(
        lst.contains(
            new DbNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N')));
    assertTrue(
        lst.contains(
            new DbNode(
                "NELEV00X05", 1250, 850, 5, "Not Faulkner", "ELEV", "Elev X", "Hall 7", 'N')));
    MapDB.deleteNode("NELEV00X07");
    MapDB.deleteNode("NELEV00X06");
    MapDB.deleteNode("NELEV00X05");
    MapDB.deleteNode("NHALL00105");

    assertEquals(1, MapDB.searchNode(-1, null, null, "Neurology").size());
    assertTrue(
        MapDB.searchNode(-1, null, null, "Cardiology").get(0).getNodeID().equals("NDEPT00104"));
  }

  // Chris
  @Test
  public void testGetGNode() throws DBException {
    Node sample = MapDB.getGNode("NDEPT01005");
    assertNotNull(sample);
    assertTrue(sample.getX() == 1300 && sample.getY() == 1200);
  }

  // Chris
  @Test
  public void testGetGAdjacent() throws SQLException, ClassNotFoundException, DBException {

    MapDB.addEdge("NHALL00104", "NHALL00204");
    MapDB.addEdge("NHALL00104", "NDEPT00104");
    MapDB.addEdge("NHALL00104", "NDEPT00204");
    MapDB.addNode("NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    MapDB.addNode("NELEV00X06", 1250, 850, 6, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    MapDB.addNode("NELEV00X05", 1250, 850, 5, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    MapDB.addNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addNode("NHALL00107", 1250, 850, 7, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addNode("NHALL00106", 1250, 850, 6, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addNode("NHALL00207", 1250, 850, 7, "Not faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    MapDB.addEdge("NELEV00X07", "NELEV00X06");
    MapDB.addEdge("NELEV00X05", "NELEV00X06");
    MapDB.addEdge("NHALL00105", "NELEV00X05");
    MapDB.addEdge("NHALL00106", "NELEV00X06");
    MapDB.addEdge("NHALL00107", "NELEV00X07");
    MapDB.addEdge("NHALL00107", "NHALL00207");
    LinkedList<Node> lst = MapDB.getGAdjacent("NELEV00X06", 5, 7);
    assertTrue(
        lst.contains(new Node(1250, 850, "NELEV00X07"))
            && lst.contains(new Node(1250, 850, "NELEV00X05")));
    assertFalse(lst.contains(new Node(1250, 850, "NHALL00106")));

    lst = MapDB.getGAdjacent("NELEV00X07", 5, 7);
    assertTrue(lst.contains(new Node(1250, 850, "NELEV00X06")));
    assertTrue(lst.contains(new Node(1250, 850, "NHALL00107")));
    lst = MapDB.getGAdjacent("NELEV00X05", 5, 7);
    assertTrue(
        lst.contains(new Node(1250, 850, "NELEV00X06"))
            && lst.contains(new Node(1250, 850, "NHALL00105")));

    LinkedList<Node> adjList = MapDB.getGAdjacent("NHALL00104");
    assertNotNull(adjList); // error here

    assertTrue(adjList.contains(new Node(1350, 1250, "NHALL00204")));
    assertTrue(adjList.contains(new Node(1350, 950, "NDEPT00104")));
    assertTrue(adjList.contains(new Node(1450, 950, "NDEPT00204")));

    assertEquals(3, adjList.size());
    MapDB.removeEdge("NHALL00104", "NHALL00204");
    MapDB.removeEdge("NHALL00104", "NDEPT00104");
    MapDB.removeEdge("NHALL00104", "NDEPT00204");
    MapDB.deleteNode("NELEV00X07");
    MapDB.deleteNode("NELEV00X06");
    MapDB.deleteNode("NELEV00X05");
    MapDB.deleteNode("NHALL00105");
    MapDB.deleteNode("NHALL00107");
    MapDB.deleteNode("NHALL00106");
    MapDB.deleteNode("NHALL00207");
  }

  // Chris
  @Test
  public void testFloorNodes() throws DBException {
    LinkedList<DbNode> nodeList = MapDB.floorNodes(4, "Faulkner");
    assertEquals(6, nodeList.size());
    // assertTrue(nodeList.get(0).getNodeID().equals("NDEPT01005"));
  }

  // Nick
  @Test
  public void testVisNodes() throws DBException {
    LinkedList<DbNode> vis = MapDB.visNodes(4, "Faulkner");
    assertNotNull(vis);
    assertEquals(2, vis.size());

    assertTrue(
        vis.contains(
            new DbNode(
                "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N')));
    assertTrue(
        vis.contains(
            new DbNode(
                "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N')));
  }

  // Nick
  @Test
  public void testAllNodes() throws DBException {
    LinkedList<DbNode> all = MapDB.allNodes();
    assertNotNull(all);
    assertEquals(5, all.size());

    assertTrue(
        all.contains(
            new DbNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N')));
    assertTrue(
        all.contains(
            new DbNode(
                "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N')));
    assertTrue(
        all.contains(
            new DbNode(
                "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N')));
    assertTrue(
        all.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));
    assertTrue(
        all.contains(
            new DbNode(
                "NDEPT01005",
                1300,
                1200,
                5,
                "Faulkner",
                "DEPT",
                "Software Engineering",
                "Dept 10",
                'N')));
  }

  // Nick
  @Test
  public void testGetAdjacent() throws DBException {
    MapDB.addEdge("NHALL00104", "NHALL00204");
    MapDB.addEdge("NHALL00104", "NDEPT00104");
    MapDB.addEdge("NHALL00104", "NDEPT00204");

    LinkedList<DbNode> adj = MapDB.getAdjacent("NHALL00104");
    assertNotNull(adj);

    assertTrue(
        adj.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));
    assertTrue(
        adj.contains(
            new DbNode(
                "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N')));
    assertTrue(
        adj.contains(
            new DbNode(
                "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N')));

    assertEquals(3, adj.size());

    MapDB.removeEdge("NHALL00104", "NHALL00204");
    MapDB.removeEdge("NHALL00104", "NDEPT00104");
    MapDB.removeEdge("NHALL00104", "NDEPT00204");
  }

  // Nick
  @Test
  public void testAddEdge() throws DBException {
    assertTrue(MapDB.addEdge("NHALL00104", "NHALL00204"));

    LinkedList<DbNode> adj = MapDB.getAdjacent("NHALL00204");
    assertNotNull(adj);
    assertTrue(
        adj.contains(
            new DbNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N')));

    adj = MapDB.getAdjacent("NHALL00104");
    assertNotNull(adj);
    assertTrue(
        adj.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));

    MapDB.removeEdge("NHALL00104", "NHALL00204");
  }

  // Nick
  @Test
  public void testRemoveEdge() throws DBException {
    MapDB.addEdge("NHALL00204", "NDEPT00104");

    assertTrue(MapDB.removeEdge("NHALL00204", "NDEPT00104"));

    LinkedList<DbNode> adj = MapDB.getAdjacent("NHALL00204");
    assertNotNull(adj);
    assertFalse(
        adj.contains(
            new DbNode(
                "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N')));

    adj = MapDB.getAdjacent("NDEPT00104");
    assertNotNull(adj);
    assertFalse(
        adj.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));
  }

  @Test
  public void testGetFloorEdges() throws DBException {
    MapDB.addNode("NELEV00X03", 2, 1, 3, "Faulkner", "ELEV", "Elevator X3", "ELEV X3", 'N');
    MapDB.addNode("NELEV00X04", 2, 1, 4, "Faulkner", "ELEV", "Elevator X4", "ELEV X4", 'N');

    MapDB.addEdge("NHALL00204", "NDEPT00104");
    MapDB.addEdge("NHALL00104", "NHALL00204");
    MapDB.addEdge("NELEV00X03", "NELEV00X04");

    LinkedList<DbNode[]> edges = MapDB.getFloorEdges(4, "Faulkner");

    DbNode hall1 = MapDB.getNode("NHALL00104");
    DbNode hall2 = MapDB.getNode("NHALL00204");
    DbNode dept1 = MapDB.getNode("NDEPT00104");

    assertEquals(2, edges.size());
    // there is probably a better way to test this
    assertEquals(hall1, edges.get(0)[0]);
    assertEquals(hall2, edges.get(0)[1]);
    assertEquals(hall2, edges.get(1)[0]);
    assertEquals(dept1, edges.get(1)[1]);

    MapDB.removeEdge("NHALL00204", "NDEPT00104");
    MapDB.removeEdge("NHALL00104", "NHALL00204");
    MapDB.removeEdge("NELEV00X03", "NELEV00X04");

    MapDB.deleteNode("NELEV00X03");
    MapDB.deleteNode("NELEV00X04");
  }

  @AfterAll
  public static void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}
