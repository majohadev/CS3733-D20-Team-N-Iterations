package edu.wpi.N.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NodeMethodsTest {

  /** Tests that compareTo method will return -1 since score of Node1 is less than score of Node2 */
  @Test
  public void compareSmallerWithBiggerScore() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNode");
    testNodeOne.score = 5;
    Node testNodeTwo = new Node(6.3, 2.3, "DifferentName");
    testNodeTwo.score = 6;

    // Score of testNodeOne is smaller than testNodeTwo score
    Assertions.assertEquals(testNodeOne.compareTo(testNodeTwo), -1);
  }

  /** Tests that compareTo method will return 1 since score of Node1 > score of Node2 */
  @Test
  public void compareBiggerWithSmallerScore() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNode");
    testNodeOne.score = 6;
    Node testNodeTwo = new Node(6.3, 2.3, "DifferentName");
    testNodeTwo.score = 5;

    Assertions.assertEquals(testNodeOne.compareTo(testNodeTwo), 1);
  }

  /** Tests that compareTo method will return 0, since 2 Nodes have equal scores */
  @Test
  public void compareSameScores() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNode");
    testNodeOne.score = 5;
    Node testNodeTwo = new Node(6.3, 2.3, "DifferentName");
    testNodeTwo.score = 5;

    Assertions.assertEquals(testNodeOne.compareTo(testNodeTwo), 0);
  }

  /** Tests that compareTo method will throw NullPointerException if Node1 is compared with Null */
  @Test
  public void compareWithNull() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNode");
    testNodeOne.score = 5;

    Assertions.assertThrows(NullPointerException.class, () -> testNodeOne.compareTo(null));
  }

  /** Tests that equals method returns true if IDs of the two nodes are the same */
  @Test
  public void testNodesEqual() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNodeOne");
    Node testNodeTwo = new Node(1.1, 6.7, "TestNodeOne");

    Assertions.assertEquals(testNodeOne.equals(testNodeTwo), true);
  }

  /** Tests that equals method returns false if IDs of the two nodes are NOT the same */
  @Test
  public void testNodesDoNotEqual() {
    Node testNodeOne = new Node(5.5, 6.7, "TestNodeOne");
    Node testNodeTwo = new Node(1.1, 6.7, "TestNodeTwo");

    Assertions.assertEquals(testNodeOne.equals(testNodeTwo), false);
  }

  /** Tests that equals method returns false if we pass Null as a parameter */
  @Test
  public void testEqualToNull() {
    Node testNodeOne = new Node(1, 2, "TestNodeOne");
    Assertions.assertEquals(testNodeOne.equals(null), false);
  }
}
