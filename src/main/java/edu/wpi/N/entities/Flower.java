package edu.wpi.N.entities;

public class Flower {

  String flowerName;
  String price;

  public Flower(String name, int price) {
    this.flowerName = name;
    this.price = "$" + String.format("%.2f", ((double) price) / 100);
  }

  public String getFlowerName() {
    return flowerName;
  }

  public String getPrice() {
    return price;
  }
}
