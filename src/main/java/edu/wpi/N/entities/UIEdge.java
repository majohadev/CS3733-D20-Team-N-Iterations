package edu.wpi.N.entities;

import edu.wpi.N.views.MapBaseController;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class UIEdge {

  private UINode nodeA, nodeB;
  private boolean selected;
  private boolean highlighted;
  private Line marker;
  private MapBaseController mbc;

  private final Color colorNormal = Color.BLACK;
  private final Color colorSelected = Color.LAWNGREEN;

  public UIEdge(boolean showing, UINode nodeA, UINode nodeB) {

    marker = new Line();
    marker.setStrokeWidth(2);
    marker.setStroke(colorNormal);
    marker.setStrokeLineCap(StrokeLineCap.ROUND);
    marker.setFill(colorNormal);
    // marker.setOpacity(0.7);
    marker.setOnMouseClicked(mouseEvent -> this.onMarkerClicked(mouseEvent));
    marker.setCursor(Cursor.HAND); // Cursor points when over nodes
    this.selected = false;
    this.highlighted = false;
    setVisible(showing);

    setNodes(nodeA, nodeB);
  }

  public void updateMarkerPos() {
    marker.setStartX(nodeA.getX());
    marker.setStartY(nodeA.getY());
    marker.setEndX(nodeB.getX());
    marker.setEndY(nodeB.getY());
  }

  public boolean toggleSelected() {
    setSelected(!this.selected);
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      // Selected appearance
      marker.setStroke(colorSelected);
    } else {
      // Deselected appearance
      marker.setStroke(colorNormal);
    }
  }

  public boolean getSelected() {
    return selected;
  }

  public void setBaseMap(MapBaseController mbc) {
    this.mbc = mbc;
  }

  public void onMarkerClicked(MouseEvent e) {

    toggleSelected();
    if (mbc != null) {
      mbc.onUIEdgeClicked(e, this);
    }
  }

  public void setVisible(boolean visible) {
    marker.setVisible(visible);
  }

  public void placeOnPane(Pane pane) {
    if (marker.getParent() == null) {
      pane.getChildren().add(this.marker);
    }
  }

  public void setNodes(UINode nodeA, UINode nodeB) {
    if (nodeA != null && nodeB != null) {
      if (nodeA != nodeB && nodeA.edgeTo(nodeB) == null) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
      }
    }
  }

  // Assuming bi-directionality
  public boolean leadsTo(UINode node) {
    return (node == nodeA || node == nodeB);
  }

  // Edges are equal if they have the same end UINodes
  @Override
  public boolean equals(Object other) {
    if (other instanceof UIEdge) {
      UIEdge otherEdge = (UIEdge) other;
      return (otherEdge.nodeA == nodeA && otherEdge.nodeB == nodeB)
          || (otherEdge.nodeB == nodeA && otherEdge.nodeA == nodeB);
    }
    return false;
  }
}
