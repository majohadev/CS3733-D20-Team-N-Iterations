package edu.wpi.N.database;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.DbNode;
import edu.wpi.N.entities.Node;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DbControllerTest {
  @BeforeAll
  public static void setup() throws SQLException, ClassNotFoundException, DBException {
    DbController.initDB();
    DbController.addNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addNode(
        "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
    DbController.addNode(
        "NDEPT00204", 1450, 950, 4, "Faulkner", "DEPT", "Neurology", "Dept 2", 'N');
    DbController.addNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N');
    DbController.addNode(
        "NDEPT01005", 1300, 1200, 5, "Faulkner", "DEPT", "Software Engineering", "Dept 10", 'N');
  }

  // Noah
  @Test
  public void testAddNodeID() throws DBException {
    assertTrue(
        DbController.addNode(
            "NHALL01404", 771, 123, 4, "Faulkner", "HALL", "HALL 14", "Hall 14", 'N'));
    DbController.deleteNode("NHALL01404");
  }

  // Noah
  @Test
  public void testModifyNode() throws DBException {
    DbController.addEdge("NHALL00204", "NHALL00104");
    DbController.modifyNode("NHALL00204", 123, 771, 4, "Faulkner", "CONF", "DEPT 3", "Dept 3", 'N');
    // DbTester.printDB();
    DbNode n = DbController.getNode("NCONF00104");
    assertEquals(123, n.getX());
    assertEquals("CONF", n.getNodeType());
    DbController.deleteNode("NCONF00104");
    DbController.addNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N');
  }

  @Test
  public void testSafeModifyNode() throws DBException {
    DbController.modifyNode("NDEPT00104", 1350, 420, "Cardio", "Dept X");
    DbNode n = DbController.getNode("NDEPT00104");
    assertEquals(1350, n.getX());
    assertEquals(420, n.getY());
    assertEquals("Faulkner", n.getBuilding());
    assertEquals("Cardio", n.getLongName());
    assertEquals("Dept X", n.getShortName());
    DbController.deleteNode("NDEPT00104");
    DbController.addNode(
        "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N');
  }

  // Noah
  @Test
  public void testMoveNode() throws DBException {
    DbController.moveNode("NHALL00204", 135, 445);
    DbNode n = DbController.getNode("NHALL00204");
    assertTrue(n.getX() == 135 && n.getY() == 445);
    DbController.moveNode("NHALL00204", 1350, 1250);
  }

  // Noah
  @Test
  public void testDeleteNode() throws DBException {
    DbController.addNode("NHALL00704", 1250, 850, 4, "Faulkner", "HALL", "Hall 7", "Hall 7", 'N');
    DbController.deleteNode("NHALL00704");
    assertNull(DbController.getNode("NHALL00704"));
  }

  // Noah
  @Test
  public void testGetNode() throws DBException {
    DbNode n = DbController.getNode("NDEPT00204");
    assertTrue(n.getX() == 1450 && n.getY() == 950);
  }

  // Chris
  @Test
  // TODO: just see if it contains
  public void testAddNodeNoID() throws DBException {
    /*DbController.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7");
    assertTrue(DbController.floorNodes(4, "Faulkner").contains());*/
    assertNotNull(DbController.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7"));
    DbNode node = DbController.addNode(1300, 1200, 4, "Faulkner", "DEPT", "Database", "Dept 7");
    assertTrue(node.getNodeID() != null);
    assertTrue(
        DbController.allNodes()
            .get(DbController.allNodes().size() - 1)
            .getLongName()
            .equals("Database"));
  }

  // Chris
  @Test
  public void testSearchNode() throws DBException {
    // need to search by floor, building, nodeType, longName.
    DbController.addNode(
        "NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elevator X", "Hall 7", 'N');
    DbController.addNode("NELEV00X06", 1250, 850, 7, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    DbController.addNode(
        "NELEV00X05", 1250, 850, 5, "Not Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    DbController.addNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "ELEV X", "Hall 1", 'N');

    LinkedList<DbNode> lst = DbController.searchNode(7, "Faulkner", "ELEV", "Elev X");
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
    lst = DbController.searchNode(-1, null, "ELEV", null);
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
    lst = DbController.searchNode(5, null, null, "ElEv");
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
    lst = DbController.searchVisNode(5, null, null, "ElEv");
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
    DbController.deleteNode("NELEV00X07");
    DbController.deleteNode("NELEV00X06");
    DbController.deleteNode("NELEV00X05");
    DbController.deleteNode("NHALL00105");

    assertEquals(1, DbController.searchNode(-1, null, null, "Neurology").size());
    assertTrue(
        DbController.searchNode(-1, null, null, "Cardiology")
            .get(0)
            .getNodeID()
            .equals("NDEPT00104"));
  }

  // Chris
  @Test
  public void testGetGNode() throws DBException {
    Node sample = DbController.getGNode("NDEPT01005");
    assertNotNull(sample);
    assertTrue(sample.getX() == 1300 && sample.getY() == 1200);
  }

  // Chris
  @Test
  public void testGetGAdjacent() throws SQLException, ClassNotFoundException, DBException {

    DbController.addEdge("NHALL00104", "NHALL00204");
    DbController.addEdge("NHALL00104", "NDEPT00104");
    DbController.addEdge("NHALL00104", "NDEPT00204");
    DbController.addNode("NELEV00X07", 1250, 850, 7, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    DbController.addNode("NELEV00X06", 1250, 850, 6, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    DbController.addNode("NELEV00X05", 1250, 850, 5, "Faulkner", "ELEV", "Elev X", "Hall 7", 'N');
    DbController.addNode("NHALL00105", 1250, 850, 5, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addNode("NHALL00107", 1250, 850, 7, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addNode("NHALL00106", 1250, 850, 6, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addNode(
        "NHALL00207", 1250, 850, 7, "Not faulkner", "HALL", "Hall 1", "Hall 1", 'N');
    DbController.addEdge("NELEV00X07", "NELEV00X06");
    DbController.addEdge("NELEV00X05", "NELEV00X06");
    DbController.addEdge("NHALL00105", "NELEV00X05");
    DbController.addEdge("NHALL00106", "NELEV00X06");
    DbController.addEdge("NHALL00107", "NELEV00X07");
    DbController.addEdge("NHALL00107", "NHALL00207");
    LinkedList<Node> lst = DbController.getGAdjacent("NELEV00X06", 5, 7);
    assertTrue(
        lst.contains(new Node(1250, 850, "NELEV00X07"))
            && lst.contains(new Node(1250, 850, "NELEV00X05")));
    assertFalse(lst.contains(new Node(1250, 850, "NHALL00106")));

    lst = DbController.getGAdjacent("NELEV00X07", 5, 7);
    assertTrue(lst.contains(new Node(1250, 850, "NELEV00X06")));
    assertTrue(lst.contains(new Node(1250, 850, "NHALL00107")));
    lst = DbController.getGAdjacent("NELEV00X05", 5, 7);
    assertTrue(
        lst.contains(new Node(1250, 850, "NELEV00X06"))
            && lst.contains(new Node(1250, 850, "NHALL00105")));

    LinkedList<Node> adjList = DbController.getGAdjacent("NHALL00104");
    assertNotNull(adjList); // error here

    assertTrue(adjList.contains(new Node(1350, 1250, "NHALL00204")));
    assertTrue(adjList.contains(new Node(1350, 950, "NDEPT00104")));
    assertTrue(adjList.contains(new Node(1450, 950, "NDEPT00204")));

    assertEquals(3, adjList.size());
    DbController.removeEdge("NHALL00104", "NHALL00204");
    DbController.removeEdge("NHALL00104", "NDEPT00104");
    DbController.removeEdge("NHALL00104", "NDEPT00204");
    DbController.deleteNode("NELEV00X07");
    DbController.deleteNode("NELEV00X06");
    DbController.deleteNode("NELEV00X05");
    DbController.deleteNode("NHALL00105");
    DbController.deleteNode("NHALL00107");
    DbController.deleteNode("NHALL00106");
    DbController.deleteNode("NHALL00207");
  }

  // Chris
  @Test
  public void testFloorNodes() throws DBException {
    LinkedList<DbNode> nodeList = DbController.floorNodes(4, "Faulkner");
    assertEquals(6, nodeList.size());
    // assertTrue(nodeList.get(0).getNodeID().equals("NDEPT01005"));
  }

  // Nick
  @Test
  public void testVisNodes() throws DBException {
    LinkedList<DbNode> vis = DbController.visNodes(4, "Faulkner");
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
    LinkedList<DbNode> all = DbController.allNodes();
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
    DbController.addEdge("NHALL00104", "NHALL00204");
    DbController.addEdge("NHALL00104", "NDEPT00104");
    DbController.addEdge("NHALL00104", "NDEPT00204");

    LinkedList<DbNode> adj = DbController.getAdjacent("NHALL00104");
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

    DbController.removeEdge("NHALL00104", "NHALL00204");
    DbController.removeEdge("NHALL00104", "NDEPT00104");
    DbController.removeEdge("NHALL00104", "NDEPT00204");
  }

  // Nick
  @Test
  public void testAddEdge() throws DBException {
    assertTrue(DbController.addEdge("NHALL00104", "NHALL00204"));

    LinkedList<DbNode> adj = DbController.getAdjacent("NHALL00204");
    assertNotNull(adj);
    assertTrue(
        adj.contains(
            new DbNode("NHALL00104", 1250, 850, 4, "Faulkner", "HALL", "Hall 1", "Hall 1", 'N')));

    adj = DbController.getAdjacent("NHALL00104");
    assertNotNull(adj);
    assertTrue(
        adj.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));

    DbController.removeEdge("NHALL00104", "NHALL00204");
  }

  // Nick
  @Test
  public void testRemoveEdge() throws DBException {
    DbController.addEdge("NHALL00204", "NDEPT00104");

    assertTrue(DbController.removeEdge("NHALL00204", "NDEPT00104"));

    LinkedList<DbNode> adj = DbController.getAdjacent("NHALL00204");
    assertNotNull(adj);
    assertFalse(
        adj.contains(
            new DbNode(
                "NDEPT00104", 1350, 950, 4, "Faulkner", "DEPT", "Cardiology", "Dept 1", 'N')));

    adj = DbController.getAdjacent("NDEPT00104");
    assertNotNull(adj);
    assertFalse(
        adj.contains(
            new DbNode("NHALL00204", 1350, 1250, 4, "Faulkner", "HALL", "Hall 2", "Hall 2", 'N')));
  }

  @AfterAll
  public static void clearDB() throws DBException {
    DbController.clearNodes();
  }
}
