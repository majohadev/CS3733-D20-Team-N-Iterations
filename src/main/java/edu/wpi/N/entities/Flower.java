package edu.wpi.N.entities;

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
  double price;

  public Flower(String name, double price) {
    this.flowerName = name;
    this.price = price;
  }

  public double getPrice() {
    return price;
  }
}
