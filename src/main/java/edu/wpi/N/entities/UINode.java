package edu.wpi.N.entities;

import edu.wpi.N.views.MapBaseController;
import java.util.LinkedList;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class UINode {

  private LinkedList<UIEdge> connectedEdges = new LinkedList<>();
  private boolean selected = false;
  private Shape marker;
  private MapBaseController mbc;

  private final Color colorNormal = Color.PURPLE;
  private final Color colorSelected = Color.RED;

  public UINode(boolean showing) {

    marker = new Circle();
    ((Circle) marker).setRadius(6);
    marker.setFill(colorNormal);
    marker.setOpacity(0.7);
    marker.setOnMouseClicked(mouseEvent -> this.toggleSelected());
    marker.setCursor(Cursor.HAND); // Cursor points when over nodes
  }

  public boolean toggleSelected() {
    setSelected(!this.selected);
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      // Selected appearance
      marker.setFill(colorSelected);
    } else {
      // Deselected appearance
      marker.setFill(colorNormal);
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

  public void setPos(int x, int y) {
    if (marker.getParent() != null) {
      this.marker.setTranslateX(x);
      this.marker.setTranslateY(y);

      for (UIEdge edge : connectedEdges) {
        edge.updateMarkerPos(); // Move all lines with the node
      }
    }
  }

  public double getX() {
    return this.marker.getTranslateX();
  }

  public double getY() {
    return this.marker.getTranslateY();
  }

  public boolean getSelected() {
    return selected;
  }

  public void setBaseMap(MapBaseController mbc) {
    this.mbc = mbc;
  }

  public void onMarkerClicked(MouseEvent e) {
    if (mbc != null) {
      mbc.onUINodeClicked(e, this);
    }
  }

  // Return added edge
  public UIEdge addEdgeTo(UINode other) {

    if (edgeTo(other) == null) {
      UIEdge newEdge = new UIEdge(false, this, other);
      this.connectedEdges.add(newEdge);
      other.connectedEdges.add(newEdge);
      newEdge.updateMarkerPos();
      return newEdge;
    }
    return null; // Edge exists already
  }

  // Return whether edge was broken
  public boolean breakEdgeTo(UINode other) {
    UIEdge toBreak = edgeTo(other);
    if (toBreak != null) {
      connectedEdges.remove(toBreak);
      other.connectedEdges.remove(toBreak);
      return true;
    }
    return false; // Edge does not exist
  }

  // Assuming bi-directionality
  public UIEdge edgeTo(UINode other) {
    for (UIEdge edge : connectedEdges) {
      if (edge.leadsTo(other)) {
        return edge;
      }
    }
    return null;
  }
}
