package edu.wpi.N.algorithms;

public class GraphMethodsTest {
  //
  //  InputStream input = Main.class.getResourceAsStream("csv/MapCoordinates.csv");
  //  CSVParser parser = new CSVParser();
  //  Graph testGraph = parser.parseCSV(input);
  //  Node startNode = testGraph.getNode("MOHSClinic");
  //  Node endNode = testGraph.getNode("HVMANeurology");
  //  Pathfinder newPath = new Pathfinder(testGraph, startNode, endNode);
  //  Path myPath = newPath.findPath();
  //
  //  /** Tests that getEdges(nodeID) returns the correct list of edges for a given node */
  //  @Test
  //  public void getEdgesTester() {
  //    LinkedList<String> hall1Edges = new LinkedList<String>();
  //    hall1Edges.add("MOHSClinic");
  //    hall1Edges.add("HALL2");
  //    Assertions.assertEquals(testGraph.getEdges("HALL1"), hall1Edges);
  //
  //    LinkedList<String> hall3Edges = new LinkedList<String>();
  //    hall3Edges.add("HALL2");
  //    hall3Edges.add("HALL4");
  //    Assertions.assertEquals(testGraph.getEdges("HALL3"), hall3Edges);
  //
  //    LinkedList<String> neurologyEdges = new LinkedList<String>();
  //    neurologyEdges.add("HALL6");
  //    Assertions.assertEquals(testGraph.getEdges("Neurology"), neurologyEdges);
  //  }
  //
  //  /**
  //   * Tests that getEdges(nodeID) will return null if the node is not in the graph and if the
  // node
  //   * does not have any edges
  //   */
  //  @Test
  //  public void getEdgesNullTester() {
  //    Node testNode = new Node(2.345, 5.5657, "TESTNODE");
  //    assertNull(testGraph.getEdges("TESTNODE"));
  //
  //    testGraph.addNode(testNode);
  //    assertNull(testGraph.getEdges("TESTNODE"));
  //  }
  //
  //  /**
  //   * Tests that addEdges(nodeID1,nodeID2) will add the given edge to the list of nodes for both
  //   * given nodes
  //   */
  //  @Test
  //  public void addEdgesTester() {
  //    LinkedList<String> hall1Edges = new LinkedList<String>();
  //    hall1Edges.add("MOHSClinic");
  //    hall1Edges.add("HALL2");
  //    hall1Edges.add("HALL3");
  //    testGraph.addEdge("HALL1", "HALL3");
  //    Assertions.assertEquals(testGraph.getEdges("HALL1"), hall1Edges);
  //
  //    LinkedList<String> hall3Edges = new LinkedList<String>();
  //    hall3Edges.add("HALL2");
  //    hall3Edges.add("HALL4");
  //    hall3Edges.add("HALL1");
  //    Assertions.assertEquals(testGraph.getEdges("HALL3"), hall3Edges);
  //  }
  //
  //  /**
  //   * Tests that addEdges(nodeID1,nodeID2) will create a list of edges for a node that currently
  // has
  //   * no edges
  //   */
  //  @Test
  //  public void addEdgesEmptyNodeTester() {
  //    testGraph.addEdge("TESTNODE", "Elevator");
  //    LinkedList<String> testNodeEdges = new LinkedList<String>();
  //    testNodeEdges.add("Elevator");
  //    Assertions.assertEquals(testGraph.getEdges("TESTNODE"), testNodeEdges);
  //  }
  //
  //  /**
  //   * Tests that a duplicate edge will not be added if you run addEdge(nodeID1,nodeID2) on an
  // edge
  //   * that already exists
  //   */
  //  @Test
  //  public void addEdgeAlreadyThereTester() {
  //    LinkedList<String> neurologyEdges = new LinkedList<String>();
  //    neurologyEdges.add("HALL6");
  //    LinkedList<String> hall6Edges = new LinkedList<String>();
  //    hall6Edges.add("HALL5");
  //    hall6Edges.add("HALL7");
  //    hall6Edges.add("Neurology");
  //    testGraph.addEdge("HALL6", "Neurology");
  //    Assertions.assertEquals(testGraph.getEdges("Neurology"), neurologyEdges);
  //    Assertions.assertEquals(testGraph.getEdges("HALL6"), hall6Edges);
  //  }
  //
  //  /** Tests that heuristic(currNode, endNode) returns the correct calculated value */
  //  @Test
  //  public void heuristicTester() {
  //    Assertions.assertEquals(
  //        newPath.heuristic(testGraph.getNode("MOHSClinic"), testGraph.getNode("HALL1")),
  //        0.77,
  //        0.0001);
  //  }
  //
  //  /**
  //   * Tests that heuristic(currNode, endNode) returns the correct calculated value for a node
  // that is
  //   * not in the graph
  //   */
  //  @Test
  //  public void heuristicNotInGraphTester() {
  //    Node testNode1 = new Node(1, 0, "TESTNODE1");
  //    Node testNode2 = new Node(0, 1, "TESTNODE2");
  //    Assertions.assertEquals(newPath.heuristic(testNode1, testNode2), 2, 0.0001);
  //  }
  //
  //  /**
  //   * Tests that getNode(nodeID) returns the correct node when given a nodeID that is in the
  // graph
  //   */
  //  @Test
  //  public void getNodeTester() {
  //    Node testNode = new Node(5.762, 0.646, "MOHSClinic");
  //    Assertions.assertEquals(testGraph.getNode("MOHSClinic"), testNode);
  //
  //    Node testNode2 = new Node(6.532, 4.562, "HALL10");
  //    Assertions.assertEquals(testGraph.getNode("HALL10"), testNode2);
  //  }
  //
  //  /**
  //   * Tests that getNode(nodeID) returns null when given an nodeID that isn't in the graph or
  // doesn't
  //   * exist at all
  //   */
  //  @Test
  //  public void getNodeNullTester() {
  //    // Call getNode on node that doesn't exist at all
  //    assertNull(testGraph.getNode("test1"));
  //
  //    // Call getNode on node that exists but not isn't in the graph
  //    Node testNode2 = new Node(6.5, 2.0, "test2");
  //    assertNull(testGraph.getNode("test2"));
  //  }
  //
  //  /** Tests that addNode(node) adds the given node to the graph */
  //  @Test
  //  public void addNodeTester() {
  //    Node testNode = new Node(7.3, 4.6, "testNode1");
  //    testGraph.addNode(testNode);
  //    Assertions.assertEquals(testGraph.getNode("testNode1"), testNode);
  //
  //    Node testNode2 = new Node(10.8, 5.5, "testNode1");
  //    testGraph.addNode(testNode2);
  //    Assertions.assertEquals(testGraph.getNode("testNode1"), testNode2);
  //  }
  //
  //  /**
  //   * Tests that addNode(node) doesn't add the given node to the graph if it has the same ID as
  //   * another node in the graph (future test to implement once functionality is added)
  //   */
  //
  //  /** Tests that cost(currNode, nextNode) returns the correct cost value for nodes in the graph
  // */
  //  @Test
  //  public void costTester() {
  //    Assertions.assertEquals(
  //        newPath.cost(testGraph.getNode("MOHSClinic"), testGraph.getNode("Neurology")),
  //        2.641,
  //        0.005);
  //  }
  //
  //  /**
  //   * Tests that cost(currNode, nextNode) returns the correct value for nodes even if they aren't
  // in
  //   * a graph
  //   */
  //  @Test
  //  public void costNotInGraphTester() {
  //    Node testNode = new Node(0, 0, "node1");
  //    Node testNode2 = new Node(3, 4, "node2");
  //    Assertions.assertEquals(newPath.cost(testNode, testNode2), 5, 0.0001);
  //  }
}
