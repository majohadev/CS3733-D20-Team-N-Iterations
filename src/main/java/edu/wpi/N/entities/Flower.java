
package edu.wpi.N.entities;

import java.util.LinkedList;

public class Flower {
    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    String flowerName;
    int price;

    public Flower(String name, int price){
        this.flowerName = name;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }






}
