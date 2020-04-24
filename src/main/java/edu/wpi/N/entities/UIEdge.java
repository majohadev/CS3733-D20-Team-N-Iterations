package edu.wpi.N.entities;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.lang.reflect.Array;
import java.util.LinkedList;

public class UIEdge {
    Line line;
    LinkedList<DbNode> nodes;

    public UIEdge(LinkedList nodes) {
        this.nodes = nodes;
    }

    public void setLine (Line line) {
        this.line = line;
    }

    public Line getLine () {
        return this.line;
    }

    public void setDBNodes(LinkedList<DbNode> nodes) {
        this.nodes = nodes;
    }

    public LinkedList<DbNode> getDBNodes() {
        return this.nodes;
    }



}
